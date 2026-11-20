package com.example.backend.handler;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.io.WKBReader;
import org.locationtech.jts.io.WKBWriter;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class GeometryTypeHandler extends BaseTypeHandler<Geometry> {

    // 1. 创建一个标准的几何工厂
    private static final GeometryFactory FACTORY = new GeometryFactory();
    // 2. 把工厂喂给 Reader 和 Writer，这样它们就能完美兼容 PostGIS 的 SRID 格式了
    private static final WKBReader READER = new WKBReader(FACTORY);
    private static final WKBWriter WRITER = new WKBWriter(2, true);

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, Geometry parameter, JdbcType jdbcType) throws SQLException {
        parameter.setSRID(4326);
        ps.setBytes(i, WRITER.write(parameter));
    }

    @Override
    public Geometry getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return parse(rs.getString(columnName));
    }

    @Override
    public Geometry getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return parse(rs.getString(columnIndex));
    }

    @Override
    public Geometry getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return parse(cs.getString(columnIndex));
    }

    // JDBC 驱动返回的是 EWKB 的十六进制文本，需要先解码成二进制再交给 WKBReader
    private Geometry parse(String hexEwkb) {
        if (hexEwkb == null) {
            return null;
        }
        try {
            return READER.read(WKBReader.hexToBytes(hexEwkb));
        } catch (Exception e) {
            throw new RuntimeException("解析 Geometry 失败", e);
        }
    }
}