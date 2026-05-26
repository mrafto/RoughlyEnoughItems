/*
 * This file is licensed under the MIT License, part of Roughly Enough Items.
 * Copyright (c) 2018, 2019, 2020, 2021, 2022, 2023 shedaniel
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package me.shedaniel.rei.plugin.common;

import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.registry.display.DisplayConsumer;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import me.shedaniel.rei.api.common.util.EntryStacks;
import me.shedaniel.rei.plugin.client.displays.ClientsidedCookingDisplay;
import me.shedaniel.rei.plugin.client.displays.ClientsidedCraftingDisplay;
import me.shedaniel.rei.plugin.client.displays.ClientsidedSmithingDisplay;
import me.shedaniel.rei.plugin.client.displays.ClientsidedStoneCuttingDisplay;
import me.shedaniel.rei.plugin.common.displays.DefaultSmithingDisplay;
import me.shedaniel.rei.plugin.common.displays.DefaultStoneCuttingDisplay;
import me.shedaniel.rei.plugin.common.displays.cooking.DefaultBlastingDisplay;
import me.shedaniel.rei.plugin.common.displays.cooking.DefaultSmeltingDisplay;
import me.shedaniel.rei.plugin.common.displays.cooking.DefaultSmokingDisplay;
import me.shedaniel.rei.plugin.common.displays.crafting.DefaultCustomShapedDisplay;
import me.shedaniel.rei.plugin.common.displays.crafting.DefaultCustomShapelessDisplay;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.display.FurnaceRecipeDisplay;
import net.minecraft.world.item.crafting.display.RecipeDisplayId;
import net.minecraft.world.item.crafting.display.ShapedCraftingRecipeDisplay;
import net.minecraft.world.item.crafting.display.ShapelessCraftingRecipeDisplay;
import net.minecraft.world.item.crafting.display.SmithingRecipeDisplay;
import net.minecraft.world.item.crafting.display.StonecutterRecipeDisplay;
import org.jetbrains.annotations.ApiStatus;

import java.util.List;
import java.util.Optional;

@ApiStatus.Internal
public final class RecipeDisplayFillerSupport {
    private static final float UNKNOWN_COOKING_XP = 0;
    private static final double UNKNOWN_COOKING_TIME = 200;
    
    private RecipeDisplayFillerSupport() {}
    
    public static void registerVanillaRecipeDisplays(DisplayConsumer.RecipeDisplayConsumer registry) {
        registerVanillaRecipeDisplays(registry, false);
    }
    
    public static void registerVanillaRecipeDisplays(DisplayConsumer.RecipeDisplayConsumer registry, boolean server) {
        registry.beginRecipeDisplayFiller(ShapedCraftingRecipeDisplay.class)
                .fill((display, id) -> shapedCrafting(display, id, server));
        registry.beginRecipeDisplayFiller(ShapelessCraftingRecipeDisplay.class)
                .fill((display, id) -> shapelessCrafting(display, id, server));
        registry.beginRecipeDisplayFiller(FurnaceRecipeDisplay.class)
                .filterType(FurnaceRecipeDisplay.TYPE)
                .fill((display, id) -> cooking(display, id, server));
        registry.beginRecipeDisplayFiller(StonecutterRecipeDisplay.class)
                .filterType(StonecutterRecipeDisplay.TYPE)
                .fill((display, id) -> stonecutting(display, id, server));
        registry.beginRecipeDisplayFiller(SmithingRecipeDisplay.class)
                .filterType(SmithingRecipeDisplay.TYPE)
                .fill((display, id) -> smithing(display, id, server));
    }
    
    public static Display shapedCrafting(ShapedCraftingRecipeDisplay display, Optional<RecipeDisplayId> id, boolean server) {
        if (server) {
            return new DefaultCustomShapedDisplay(
                    EntryIngredients.ofSlotDisplays(display.ingredients()),
                    List.of(EntryIngredients.ofSlotDisplay(display.result())),
                    Optional.empty(),
                    display.width(),
                    display.height()
            );
        }
        return new ClientsidedCraftingDisplay.Shaped(display, id);
    }
    
    public static Display shapelessCrafting(ShapelessCraftingRecipeDisplay display, Optional<RecipeDisplayId> id, boolean server) {
        if (server) {
            return new DefaultCustomShapelessDisplay(
                    EntryIngredients.ofSlotDisplays(display.ingredients()),
                    List.of(EntryIngredients.ofSlotDisplay(display.result())),
                    Optional.empty()
            );
        }
        return new ClientsidedCraftingDisplay.Shapeless(display, id);
    }
    
    private static Display stonecutting(StonecutterRecipeDisplay display, Optional<RecipeDisplayId> id, boolean server) {
        if (server) {
            return new DefaultStoneCuttingDisplay(
                    List.of(EntryIngredients.ofSlotDisplay(display.input())),
                    List.of(EntryIngredients.ofSlotDisplay(display.result())),
                    Optional.empty()
            );
        }
        return new ClientsidedStoneCuttingDisplay(display, id);
    }
    
    private static Display smithing(SmithingRecipeDisplay display, Optional<RecipeDisplayId> id, boolean server) {
        List<EntryIngredient> inputs = List.of(
                EntryIngredients.ofSlotDisplay(display.template()),
                EntryIngredients.ofSlotDisplay(display.base()),
                EntryIngredients.ofSlotDisplay(display.addition())
        );
        List<EntryIngredient> outputs = List.of(EntryIngredients.ofSlotDisplay(display.result()));
        if (server) {
            return new DefaultSmithingDisplay(inputs, outputs, Optional.empty());
        }
        return new ClientsidedSmithingDisplay(inputs, outputs, id);
    }
    
    private static Display cooking(FurnaceRecipeDisplay display, Optional<RecipeDisplayId> id, boolean server) {
        List<EntryIngredient> inputs = List.of(EntryIngredients.ofSlotDisplay(display.ingredient()));
        List<EntryIngredient> outputs = List.of(EntryIngredients.ofSlotDisplay(display.result()));
        Optional<Identifier> location = Optional.empty();
        if (server) {
            if (hasCraftingStation(display, Items.SMOKER)) {
                return new DefaultSmokingDisplay(inputs, outputs, location, UNKNOWN_COOKING_XP, UNKNOWN_COOKING_TIME);
            }
            if (hasCraftingStation(display, Items.BLAST_FURNACE)) {
                return new DefaultBlastingDisplay(inputs, outputs, location, UNKNOWN_COOKING_XP, UNKNOWN_COOKING_TIME);
            }
            return new DefaultSmeltingDisplay(inputs, outputs, location, UNKNOWN_COOKING_XP, UNKNOWN_COOKING_TIME);
        }
        if (hasCraftingStation(display, Items.SMOKER)) {
            return new ClientsidedCookingDisplay.Smoking(display, id);
        }
        if (hasCraftingStation(display, Items.BLAST_FURNACE)) {
            return new ClientsidedCookingDisplay.Blasting(display, id);
        }
        return new ClientsidedCookingDisplay.Smelting(display, id);
    }
    
    private static boolean hasCraftingStation(FurnaceRecipeDisplay display, Item station) {
        try {
            return EntryIngredients.testFuzzy(EntryIngredients.ofSlotDisplay(display.craftingStation()), EntryStacks.of(station));
        } catch (Throwable ignored) {
            return false;
        }
    }
}
