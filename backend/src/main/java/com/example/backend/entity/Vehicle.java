package com.example.backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.example.backend.handler.GeometryTypeHandler; // 导入刚写的 Handler
import com.example.backend.json.PointJsonSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import org.locationtech.jts.geom.Point;

import java.time.LocalDateTime;

@Data
@TableName(value = "vehicle", autoResultMap = true)   // ← 改动1：加 autoResultMap = true
public class Vehicle {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String plateNo;

    // 引入 JTS 的 Point 类型，对应数据库里的 GEOMETRY(Point, 4326)
    @TableField(typeHandler = GeometryTypeHandler.class)  // ← 改动2：指定翻译官
    @JsonSerialize(using = PointJsonSerializer.class)     // JSON 输出成 GeoJSON
    private Point geom;

    private LocalDateTime createdAt;
}