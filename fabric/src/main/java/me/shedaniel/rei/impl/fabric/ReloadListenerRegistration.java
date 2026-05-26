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

package me.shedaniel.rei.impl.fabric;

import dev.architectury.registry.ReloadListenerRegistry;
import me.shedaniel.rei.impl.common.InternalLogger;
import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.PreparableReloadListener;

public final class ReloadListenerRegistration {
    private ReloadListenerRegistration() {
    }

    public static void register(PackType type, PreparableReloadListener listener, Identifier listenerId) {
        try {
            ReloadListenerRegistry.register(type, listener, listenerId);
            return;
        } catch (AssertionError error) {
            InternalLogger.getInstance().debug("Architectury reload listener registration unavailable for " + listenerId, error);
        }

        try {
            ResourceLoader.get(type).registerReloadListener(listenerId, listener);
            InternalLogger.getInstance().info("Registered " + listenerId + " reload listener via Fabric API.");
        } catch (Throwable error) {
            InternalLogger.getInstance().warn("Reload listener " + listenerId + " could not be registered; continuing without automatic reload hooks.");
            InternalLogger.getInstance().debug("Reload listener registration failed.", error);
        }
    }
}
