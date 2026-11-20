package com.example.backend.controller;

import com.example.backend.entity.Vehicle;
import com.example.backend.mapper.VehicleMapper;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/vehicles")
public class VehicleController {

    // 注入我们刚才写的 Mapper（数据库操作）
    @Autowired
    private VehicleMapper vehicleMapper;

    // JTS 的几何工厂，用来帮我们创造点、线、面
    private final GeometryFactory geometryFactory = new GeometryFactory();

    // 新增车辆，并带上经纬度
    @PostMapping("/add")
    public String addVehicle(
            @RequestParam(defaultValue = "皖A·88888") String plateNo,
            @RequestParam(defaultValue = "116.397") double lng, // 默认北京故宫经度
            @RequestParam(defaultValue = "39.908") double lat // 默认北京故宫纬度
    ) {
        Vehicle v = new Vehicle();
        v.setPlateNo(plateNo); // 假车牌
        v.setCreatedAt(LocalDateTime.now());

        // 1. 把经纬度变成一个 JTS 的 Coordinate 坐标点
        Coordinate coordinate = new Coordinate(lng, lat);

        // 2. 用工厂把 Coordinate 变成 Point，并给它设置空间参考系 EPSG:4326 (代表 WGS84 经纬度)
        Point point = geometryFactory.createPoint(coordinate);
        point.setSRID(4326);

        // 3. 把点存入实体类
        v.setGeom(point);

        vehicleMapper.insert(v); // MyBatis-Plus 自带的插入方法，不需要写 SQL
        return "成功插入一辆车：" + plateNo + "，坐标：" + lng + "," + lat;
    }

    // 2. 测试接口：查询所有车
    @GetMapping("/list")
    public List<Vehicle> listVehicles() {
        // MyBatis-Plus 自带的查全部方法
        return vehicleMapper.selectList(null);
    }
}