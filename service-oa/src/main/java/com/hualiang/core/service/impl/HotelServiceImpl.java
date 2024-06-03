package com.hualiang.core.service.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hualiang.model.Hotel;
import com.hualiang.model.HotelRegisteration;
import com.hualiang.model.User;
import com.hualiang.model.util.FileUtils;
import com.hualiang.common.utils.MailUtils;
import com.hualiang.core.mapper.HotelMapper;
import com.hualiang.core.mapper.HotelRegisterationMapper;
import com.hualiang.core.mapper.UserMapper;
import com.hualiang.core.service.HotelService;

@Service
@Transactional
public class HotelServiceImpl extends ServiceImpl<HotelMapper,Hotel> implements HotelService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private HotelRegisterationMapper hotelRegisterationMapper;

    @Autowired
    private HotelMapper hotelMapper;
    
    private static String html;

    static {
        InputStream in = HotelServiceImpl.class.getResourceAsStream("/notification.xml");
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));

        StringBuffer content = new StringBuffer();
        String line;
        try {
            while ((line = bufferedReader.readLine()) != null) {
                content.append(line);
            }
            html = content.toString();
            bufferedReader.close();
        } catch (IOException e) {
            html = null;
            e.printStackTrace();
        }
    }
    
    @Override
    public Integer delete(Integer hotelId) {
        FileUtils.deleteDir(baseMapper.selectOne(new QueryWrapper<Hotel>().eq("hotel_id", hotelId)).getPath());
        return baseMapper.deleteById(hotelId);
    }

    @Override
    public IPage<Hotel> getAll(Map<String,String> map) {
        return baseMapper.getAllHotel(new Page<Hotel>(1, 5).setSize(-1), map);
    }

    
    @Override
    public Integer register(HotelRegisteration hotelRegisteration) {
        return hotelRegisterationMapper.insert(hotelRegisteration) > 0 ? hotelRegisteration.getId() : null;
    }

    @Override
    public List<HotelRegisteration> getPendingRegisteration() {
        Map<String,String> map = new HashMap<>();
        map.put("option", "status");
        map.put("value", "null");
        return hotelRegisterationMapper.getAllHotelRegisteration(map);
    }
    
    @Override
    public List<HotelRegisteration> searchRegisteration(Map<String,String> option) {
        return hotelRegisterationMapper.getAllHotelRegisteration(option);
    }
    
    @Override
    public Integer review(Integer id, Boolean status) {
        Timestamp reviewTime = new Timestamp(System.currentTimeMillis());
        HotelRegisteration hotelRegisteration = hotelRegisterationMapper.selectOne(new QueryWrapper<HotelRegisteration>().eq("id", id));
        User manager = userMapper.selectOne(new QueryWrapper<User>().eq("uid", hotelRegisteration.getManagerId()));
        int result = hotelRegisterationMapper.update(null, new UpdateWrapper<HotelRegisteration>().eq("id", id).set("status", status).set("review_time", reviewTime));
        String replacedContent;
        Hotel hotel = hotelRegisteration.getHotel();
        if (status) {
            result *= baseMapper.insert(hotel);
            replacedContent = html.replace("?", "<p>您的酒店注册申请<b>已通过审核</b>，以下是酒店的相关信息：</p><div class='code'><p>酒店ID：" + hotel.getHotelId() + "</p><p>酒店名称：" + hotel.getHotelName() + "</p><p>酒店地址：" + hotel.getAddress() + "</p><p>联系电话：" + hotel.getPhone() + "</p></div>");
        } else {
            FileUtils.deleteDir(hotel.getPath());
            replacedContent = html.replace("?", "<p>您的酒店注册申请<b>未通过审核</b>，以下是拒绝注册的相关信息：</p><div class='code'><p>还没想好写什么，反正就是不行~</p></div>");
        }
        MailUtils.sendMail(manager.getEmail(), replacedContent, "酒店注册通知");
        return result > 0 ? status ? hotel.getHotelId() : 0 : null;
    }

    @Override
    public IPage<Hotel> getHotelPage(Map<String, String> option, Integer pageNum) {
        return baseMapper.getAllHotel(new Page<Hotel>(pageNum, 5), option);
    }


    //mobile
    @Override
    public List<Hotel> selectAllHotels() {
        List<Hotel> hotels = hotelMapper.selectAllHotels();
        return hotels;
    }

    @Override
    public List<Hotel> findAvailableHotels(String address, String startDate, String endDate, int numOfGuests) {
        return hotelMapper.findAvailableHotels(address, startDate, endDate, numOfGuests);
    }
}
