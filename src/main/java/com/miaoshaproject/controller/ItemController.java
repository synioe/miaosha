package com.miaoshaproject.controller;

import com.miaoshaproject.controller.viewobject.ItemVO;
import com.miaoshaproject.error.BusinessException;
import com.miaoshaproject.response.CommonReturnType;
import com.miaoshaproject.service.CacheService;
import com.miaoshaproject.service.ItemService;
import com.miaoshaproject.service.PromoService;
import com.miaoshaproject.service.model.ItemModel;
import org.joda.time.format.DateTimeFormat;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Controller("/item")
@RequestMapping("/item")
@CrossOrigin(allowCredentials = "true", origins = {"*"})
public class ItemController extends BaseController {

    @Autowired
    private ItemService itemService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private CacheService cacheService;

    @Autowired
    private PromoService promoService;

    // 创建商品的controller
    @RequestMapping(value = "/create", method = {RequestMethod.POST}, consumes = {CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType createItem(@RequestParam(name = "title") String title,
                                       @RequestParam(name = "description") String description,
                                       @RequestParam(name = "price") BigDecimal price,
                                       @RequestParam(name = "stock") Integer stock,
                                       @RequestParam(name = "imgUrl") String imgUrl) throws BusinessException {
        //封装service请求用来创建商品
        ItemModel itemModel = new ItemModel();
        itemModel.setTitle(title);
        itemModel.setDescription(description);
        itemModel.setPrice(price);
        itemModel.setStock(stock);
        itemModel.setImgUrl(imgUrl);

        ItemModel itemModelForReturn = itemService.createItem(itemModel);
        ItemVO itemVO = convertItemVOFromModel(itemModelForReturn);

        return CommonReturnType.create(itemVO);
    }

    //商品详情页浏览
    @RequestMapping(value = "/publishpromo", method = {RequestMethod.GET})
    @ResponseBody
    public CommonReturnType publishpromo(@RequestParam(name = "id") Integer id) {
        promoService.publishPromo(id);
        return CommonReturnType.create(null);
    }


    //商品详情页浏览
    @RequestMapping(value = "/get", method = {RequestMethod.GET})
    @ResponseBody
    public CommonReturnType getItem(@RequestParam(name = "id") Integer id) {

        ItemModel itemModel = null;

        //先从本地缓存中去取
        itemModel = (ItemModel) cacheService.getFromCommonCache("item_" + id);

        if (itemModel == null) {
            //根据商品Id从redis中获取
            itemModel = (ItemModel) redisTemplate.opsForValue().get("item_" + id);

            //如果redis中没有对应的itemModel，则访问下游service
            if (itemModel == null) {
                itemModel = itemService.getItemById(id);
                //存入到redis缓存中
                redisTemplate.opsForValue().set("item_" + id, itemModel);
                //设置redis失效时间 10分钟
                redisTemplate.expire("item_" + id, 10, TimeUnit.MINUTES);
            }
            //存入本地缓存
            cacheService.setCommonCache("item_" + id, itemModel);
        }

        ItemVO itemVO = convertItemVOFromModel(itemModel);

        return CommonReturnType.create(itemVO);
    }

    // 商品列表页面浏览
    @RequestMapping(value = "/list", method = {RequestMethod.GET})
    @ResponseBody
    public CommonReturnType listItem() {
        List<ItemModel> itemModelList = itemService.listItem();

        // 使用stream api将list内的itemModel 转化为 itemVO
        List<ItemVO> itemVOList = itemModelList.stream().map(itemModel -> {
            ItemVO itemVO = this.convertItemVOFromModel(itemModel);
            return itemVO;
        }).collect(Collectors.toList()); // 返回的是item VO的list
        return CommonReturnType.create(itemVOList);
    }


    private ItemVO convertItemVOFromModel(ItemModel itemModel) {
        if (itemModel == null) {
            return null;
        }
        ItemVO itemVO = new ItemVO();
        BeanUtils.copyProperties(itemModel, itemVO);
        if (itemModel.getPromoModel() != null) {
            // 有正在进行或即将进行的秒杀活动
            itemVO.setPromoStatus(itemModel.getPromoModel().getStatus());
            itemVO.setPromoPrice(itemModel.getPromoModel().getPromoItemPrice());
            itemVO.setPromoId(itemModel.getPromoModel().getId());
            itemVO.setStartDate(itemModel.getPromoModel().getStartDate().toString(DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")));
        } else {
            // 没有秒杀活动：将状态设置为0
            itemVO.setPromoStatus(0);
        }
        return itemVO;
    }
}
