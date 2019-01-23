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

package com.oglofus.protection.api;

import com.oglofus.protection.api.account.Account;
import com.oglofus.protection.api.protection.Protection;
import com.oglofus.protection.api.sender.CommandSender;
import com.oglofus.protection.api.sender.Sender;
import com.sk89q.intake.Command;
import com.sk89q.intake.CommandException;
import com.sk89q.intake.parametric.annotation.Optional;

public class ProtectionCommands {
    private final Platform platform;

    public ProtectionCommands(Platform platform) {
        this.platform = platform;
    }

    @Command(aliases = "version", desc = "Send protection's version")
    public void getVersion(@CommandSender Sender sender) {
        sender.sendMessage("Protection v" + platform.getVersion());
    }

    @Command(aliases = "info", desc = "Get information about targeted protection")
    public void getInformation(@CommandSender Sender sender, @Optional Protection protection) throws CommandException {
        if (protection == null) {
            if (sender instanceof Account) {
                protection = platform.getProtections()
                        .getProtectionOn(((Account) sender).getPosition().orElse(null))
                        .orElseThrow(() -> new CommandException("There is no protection here."));
            } else {
                throw new CommandException("Hmmm, who are you?");
            }
        }

        sender.sendMessage(protection.getName());
    }
}
