package com.github.eoniz.nexus.persistence.entity.sokoban.room;

import com.github.eoniz.nexus.model.sokoban.room.SokobanRoom;
import com.github.eoniz.nexus.persistence.entity.sokoban.level.SokobanLevelEntity;
import com.github.eoniz.nexus.persistence.entity.sokoban.level.SokobanLevelEntityMapper;
import com.github.eoniz.nexus.persistence.entity.sokoban.player.SokobanPlayerEntityMapper;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class SokobanRoomEntityMapper {
    private final SokobanLevelEntityMapper sokobanLevelEntityMapper = new SokobanLevelEntityMapper();
    private final SokobanPlayerEntityMapper sokobanPlayerEntityMapper = new SokobanPlayerEntityMapper();

    public SokobanRoom of(SokobanRoomEntity sokobanRoomEntity) {
        return SokobanRoom.builder()
                .rootMessageId(sokobanRoomEntity.getRootMessageId())
                .gameMessageId(sokobanRoomEntity.getGameMessageId())
                .owner(sokobanPlayerEntityMapper.of(sokobanRoomEntity.getOwner()))
                .sokobanLevel(sokobanLevelEntityMapper.of(sokobanRoomEntity.getSokobanLevel()))
                .build();
    }

    public SokobanRoomEntity of(SokobanRoom sokobanRoom) {
        return SokobanRoomEntity.builder()
                .rootMessageId(sokobanRoom.getRootMessageId())
                .gameMessageId(sokobanRoom.getGameMessageId())
                .owner(sokobanPlayerEntityMapper.of(sokobanRoom.getOwner()))
                .sokobanLevel(sokobanLevelEntityMapper.of(sokobanRoom.getSokobanLevel()))
                .build();
    }

}
