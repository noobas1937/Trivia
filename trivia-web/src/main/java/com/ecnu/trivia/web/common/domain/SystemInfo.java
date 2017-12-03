package com.ecnu.trivia.web.common.domain;

import com.ecnu.trivia.common.component.domain.Column;
import com.ecnu.trivia.common.component.domain.Domain;
import com.ecnu.trivia.common.component.domain.Id;
import com.ecnu.trivia.common.component.domain.Key;
import org.apache.ibatis.type.JdbcType;

import java.sql.Timestamp;

public class SystemInfo extends Domain<SystemInfo> {


    @Id(generated = true)
    @Column(jdbcType = JdbcType.INTEGER)
    private Integer id;

    @Column(value = "remote_server", jdbcType = JdbcType.VARCHAR)
    private String remoteServer;

    @Column(value = "license", jdbcType = JdbcType.VARCHAR)
    private String license;

    @Column(value = "mode", jdbcType = JdbcType.INTEGER)
    private Integer mode;

    @Column(value = "gmt_created", jdbcType = JdbcType.TIMESTAMP)
    private Timestamp gmtCreated;

    @Column(value = "gmt_modified", jdbcType = JdbcType.TIMESTAMP)
    private Timestamp gmtModified;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getRemoteServer() {
        return remoteServer;
    }

    public void setRemoteServer(String remoteServer) {
        this.remoteServer = remoteServer;
    }

    public String getLicense() {
        return license;
    }

    public void setLicense(String license) {
        this.license = license;
    }

    public Integer getMode() {
        return mode;
    }

    public void setMode(Integer mode) {
        this.mode = mode;
    }

    public Timestamp getGmtCreated() {
        return gmtCreated;
    }

    public void setGmtCreated(Timestamp gmtCreated) {
        this.gmtCreated = gmtCreated;
    }

    public Timestamp getGmtModified() {
        return gmtModified;
    }

    public void setGmtModified(Timestamp gmtModified) {
        this.gmtModified = gmtModified;
    }

    @Override
    public Key key() {
        return Key.of(id);
    }

    @Override
    public void clearKey() {
        id=0;
    }
}
