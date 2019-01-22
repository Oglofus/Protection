/*
 * Copyright 2019 Nikolaos Grammatikos <nikosgram@oglofus.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.oglofus.protection.api.modules;

import com.google.common.collect.ImmutableList;
import com.oglofus.protection.OglofusProtection;
import com.oglofus.protection.api.Platform;
import com.oglofus.protection.api.account.Account;
import com.oglofus.protection.api.cosmos.Cosmos;
import com.oglofus.protection.api.protection.MemberCategory;
import com.oglofus.protection.api.protection.Protection;
import com.oglofus.protection.api.sender.CommandSender;
import com.oglofus.protection.api.sender.Sender;
import com.sk89q.intake.argument.CommandArgs;
import com.sk89q.intake.parametric.AbstractModule;
import com.sk89q.intake.parametric.Provider;
import com.sk89q.intake.parametric.ProvisionException;
import com.sk89q.intake.parametric.provider.EnumProvider;

import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.util.List;

public class ProtectionModule extends AbstractModule {
    private final Platform platform;

    public ProtectionModule(Platform platform) {
        this.platform = platform;
    }

    @Override
    protected void configure() {
        bind(Sender.class).annotatedWith(CommandSender.class).toProvider(new Provider<Sender>() {
            @Override
            public boolean isProvided() {
                return false;
            }

            @Nullable
            @Override
            public Sender get(CommandArgs arguments, List<? extends Annotation> modifiers) throws ProvisionException {
                Sender sender = (Sender) arguments.getNamespace().get("sender");

                if (sender != null) {
                    return sender;
                } else {
                    throw new ProvisionException("Sender was set on Namespace");
                }
            }

            @Override
            public List<String> getSuggestions(String prefix) {
                return ImmutableList.of();
            }
        });
        bind(Account.class).toProvider(platform.getAccounts());
        bind(Protection.class).toProvider(platform.getProtections());
        bind(Cosmos.class).toProvider(platform.getCosmoses());
        bind(MemberCategory.class).toProvider(new EnumProvider<>(MemberCategory.class));
    }
}
