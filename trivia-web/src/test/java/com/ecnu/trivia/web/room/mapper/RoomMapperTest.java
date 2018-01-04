package com.ecnu.trivia.web.room.mapper;
//Author:guo
//Date:2018.1.4
import com.ecnu.trivia.web.room.domain.Room;
import com.ecnu.trivia.web.room.domain.vo.RoomVO;
import com.ecnu.trivia.web.utils.Constants;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.testng.AssertJUnit;

import javax.annotation.Resource;

import java.util.List;

import static org.junit.Assert.*;
@RunWith(value = SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring/applicationContext.xml"
        ,"classpath:spring/applicationContext-web.xml"})
@Transactional
public class RoomMapperTest {
    @Resource
    private RoomMapper roomMapper;



    @Test
    public void getRoomList() throws Exception {
        List res=roomMapper.getRoomList();
        if(res==null){
            AssertJUnit.fail("null");
        }

    }


    @Test
    public void getRoomById() throws Exception {
        RoomVO res= roomMapper.getRoomById(0);
        if(res!=null) {
            AssertJUnit.fail("not exist");
        }



    }


    @Test
    public void getRoomByPlayerID() throws Exception {
        Integer notPlay=0;
        Integer inPlay=79;
        Room res= roomMapper.getRoomByPlayerID(inPlay);
        if(res==null) {
            AssertJUnit.fail("exist");
        }

        Room res1= roomMapper.getRoomByPlayerID(notPlay);
        if(res1!=null) {
            AssertJUnit.fail("not exist ");
        }


    }

    @Test
    public void getRoomByUserID() throws Exception {
        Integer notPlay=1;
        Integer inPlay=2;
        Room res= roomMapper.getRoomByUserID(notPlay);
        if(res!=null)
        {
            AssertJUnit.fail("not exist");
        }
        Room res1= roomMapper.getRoomByUserID(inPlay);
        if(res1==null)
        {
            AssertJUnit.fail("exist");
        }


    }



    @Test
    public void updateRoomStatus() throws Exception {
        Integer invaildparam=2;
        roomMapper.updateRoomStatus(1, Constants.ROOM_PLAYING);
        Integer roomId_status=roomMapper.getRoomById(1).getStatus();
        if(roomId_status!=1){
            AssertJUnit.fail("fail ");
        }
        roomMapper. updateRoomStatus(3,invaildparam);
        Integer roomId_status1=roomMapper.getRoomById(3).getStatus();
        if(roomId_status1!=2){
            AssertJUnit.fail("fail ");
        }

    }

}