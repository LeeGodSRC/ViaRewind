/*
 * This file is part of ViaRewind - https://github.com/ViaVersion/ViaRewind
 * Copyright (C) 2018-2024 ViaVersion and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.viaversion.viarewind.protocol.v1_9to1_8.cooldown;

import com.viaversion.viarewind.protocol.v1_9to1_8.Protocol1_9To1_8;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.type.Types;
import com.viaversion.viaversion.libs.gson.JsonPrimitive;
import com.viaversion.viaversion.protocols.v1_8to1_9.packet.ClientboundPackets1_9;

import java.util.UUID;

public class BossBarVisualization implements CooldownVisualization {
	private final UserConnection user;
	private UUID bossUUID;

	public BossBarVisualization(UserConnection user) {
		this.user = user;
	}

	@Override
	public void show(double progress) throws Exception {
		PacketWrapper wrapper = PacketWrapper.create(ClientboundPackets1_9.BOSS_EVENT, user);
		if (bossUUID == null) {
			bossUUID = UUID.randomUUID();
			wrapper.write(Types.UUID, bossUUID);
			wrapper.write(Types.VAR_INT, 0); // Action - add
			wrapper.write(Types.COMPONENT, new JsonPrimitive(" ")); // Title
			wrapper.write(Types.FLOAT, (float) progress); // Health
			wrapper.write(Types.VAR_INT, 0); // Color
			wrapper.write(Types.VAR_INT, 0); // Division
			wrapper.write(Types.UNSIGNED_BYTE, (short) 0); // Flags
		} else {
			wrapper.write(Types.UUID, bossUUID);
			wrapper.write(Types.VAR_INT, 2); // Action - update health
			wrapper.write(Types.FLOAT, (float) progress); // Health
		}
		wrapper.send(Protocol1_9To1_8.class, false);
	}

	@Override
	public void hide() throws Exception {
		if (bossUUID == null) {
			return;
		}
		PacketWrapper wrapper = PacketWrapper.create(ClientboundPackets1_9.BOSS_EVENT, null, user);
		wrapper.write(Types.UUID, bossUUID);
		wrapper.write(Types.VAR_INT, 1); // Action - remove
		wrapper.send(Protocol1_9To1_8.class, false);
		bossUUID = null;
	}
}