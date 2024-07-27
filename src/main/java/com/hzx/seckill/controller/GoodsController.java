package com.hzx.seckill.controller;

import com.hzx.seckill.pojo.User;
import com.hzx.seckill.service.GoodsService;
import com.hzx.seckill.service.UserService;
import com.hzx.seckill.vo.GoodsVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;
import org.thymeleaf.spring5.view.reactive.ThymeleafReactiveView;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author Jools He
 * @version 1.0
 * @date 2024/7/24 22:08
 * @description: TODO
 */
@Slf4j
@Controller
@RequestMapping("/goods")
public class GoodsController {

    @Resource
    private UserService userService;    //封装到 HandlerMethodArgumentResolver 类管理
    @Resource
    private GoodsService goodsService;

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private ThymeleafViewResolver thymeleafViewResolver;

    @RequestMapping(value = "/toDetail/{goodsId}", produces = "text/html;charset=utf-8")      //uri 依据前端界面的配置
    @ResponseBody
    public String toDetail(@PathVariable(value = "goodsId") Integer goodsId,
                           User user, Model model,
                           HttpServletRequest request, HttpServletResponse response) {
        //如果用户未登录返回登录界面
        if (null == user) {
            return backToLogin(getWebContext(request, response, model));
        }
        log.info("用户:{} - 访问商品 {} 详情页面", user.getNickname(), goodsId);

        //查看是否已经缓存
        ValueOperations valueOperations = redisTemplate.opsForValue();
        //基于 key 值: goodsDetail + 商品Id 来获取
        String html = (String) valueOperations.get("goodsDetail:" + goodsId);
        if (StringUtils.hasText(html)) {
            log.info("查询到 goodsDetail:" + goodsId + "页面缓存记录，返回缓存数据");
            return html;
        }

        //基于Id查询商品详情
        GoodsVo goodsVo = null;
        try {
            goodsVo = goodsService.getGoodsVoByGoodsId(goodsId);
        } catch (Exception e) {
            log.error("Error occurred during getGoodsVoByGoodsId() process");
            return "goodsList";
        }


        int secKillStatus = 0;  //记录秒杀是否开始，默认为 0 表示未开始
        long remainSeconds = 0;  //记录秒杀开始的剩余时间
        Date endDate = goodsVo.getEndDate();
        Date startDate = goodsVo.getStartDate();
        Date now = new Date();

        if (startDate.after(now)) {  //如果未开始，状态设置为 0
            //计算距离秒杀开始时间的期限; 将 ms 转换成 s
            remainSeconds = (startDate.getTime() - now.getTime()) / 1000;
        } else if (startDate.before(now) && endDate.after(now)) {
            secKillStatus = 1;  //如果秒杀正在开始，则设置为 1
        } else if (endDate.before(now)) {
            secKillStatus = 2;  //如果秒杀已经结束，设置为 2
            remainSeconds = -1;
        }

        log.info("秒杀状态：{}, 剩余时间: {}", secKillStatus, remainSeconds);

        //将查询到的商品信息通过 model 携带到商品详情界面
        //attributeName 依据前端界面设置编写
        model.addAttribute("goods", goodsVo);
        model.addAttribute("user", user);
        model.addAttribute("secKillStatus", secKillStatus);
        model.addAttribute("remainSeconds", remainSeconds);

        //如果没有缓存数据，添加缓存
        WebContext webContext = getWebContext(request, response, model);

        //解析静态资源内的 goodsList.html 文件
        html = thymeleafViewResolver.getTemplateEngine().process("goodsDetail", webContext);

        //如果存在静态资源,则进行缓存
        if (StringUtils.hasText(html)) {
            //每过 60s 就去更新一次 Redis 页面缓存，即 60s 后该页面的缓存失效，Redis 会清除该缓存
            valueOperations.set(
                    "goodsDetail:" + goodsId,
                    html,
                    60,
                    TimeUnit.SECONDS
            );
            log.info("Redis 缓存 goodsDetail 页面成功");
        }
        return html;
    }

    //跳转到商品列表界面
//    @RequestMapping("/list")
//    public String toGoodsList(User user, Model model/*, @CookieValue(value = "userTicket") String userTicket*/) {
////        //1.判断是否携带 Cookie
////        if (!StringUtils.hasText(userTicket)) {  //如果用户未登录，返回登录
////            return "login";
////        }
////        //2.判断是否能够获取到登录的 User
////
////        /*User user = (User) request.getSession().getAttribute(ticket);*/
////        //从 Redis 存储的数据中获取
////        log.info("请求查询携带的 userTicket:{}", userTicket);
////        User user = userService.getUserByTicket(userTicket, request, response);
//
//        //使用 WebMvcConfigurer 简化上述逻辑
//
//        if (null == user) {     //如果未获取到登录用户，返回登录
//            return "login";
//        }
//        log.info("刷新登录用户携带userTicket的Cookie时间, user:{}", user.getNickname());
//
//        //将 User 信息携带到商品列表页面
//        model.addAttribute("user", user);
//
//        //将商品信息携带到商品列表页面
//        List<GoodsVo> goodsVos = goodsService.listGoodsVo();
//        if (goodsVos.isEmpty()) {
//            log.warn("请求到的商品数据为空！！");
//        }
//
//        model.addAttribute("goodsList", goodsVos);
//
//        //3.跳转到商品页面
//        return "goodsList";
//    }

    //将商品列表界面缓存到Redis内;
    //如果Redis已经存储，从Redis内取出并返回
    /*
    解读
    - @RequestMapping 的 produces:
      制定了返回内容的 MIME 类型和字符编码
      text/html;charset=utf-8 表示返回的内容类型是 HTML 文本，字符编码为 UTF-8。
     */
    @RequestMapping(value = "/list", produces = "text/html;charset=utf-8")
    @ResponseBody
    public String toGoodsList(User user, Model model,
                              HttpServletRequest request,
                              HttpServletResponse response) {

        if (null == user) {     //如果未获取到登录用户，返回登录
            return backToLogin(getWebContext(request, response, model));
        }
        log.info("刷新登录用户携带userTicket的Cookie时间, user:{}", user.getNickname());

        //查询 Redis 内是否存储了商品列表页面
        ValueOperations valueOperations = redisTemplate.opsForValue();
        String html = (String) valueOperations.get("goodsList");
        if (StringUtils.hasText(html)) {    //如果存在该页面缓存，则返回
            log.info("Redis 内已缓存静态资源界面");
            return html;
        } else {
            log.info("Redis 内未缓存静态资源界面");
        }

        //将 User 信息携带到商品列表页面
        model.addAttribute("user", user);

        //将商品信息携带到商品列表页面
        List<GoodsVo> goodsVos = goodsService.listGoodsVo();
        if (goodsVos.isEmpty()) {
            log.warn("请求到的商品数据为空！！");
        }
        model.addAttribute("goodsList", goodsVos);

        //将商品列表界面缓存到Redis内，下次直接从 Redis 内取出返回
        /*
         解读:
         - 是 Thymeleaf 模板引擎的上下文，用于在模板解析时提供必要的上下文信息。
         - 创建 WebContext 对象时，需要传递 HttpServletRequest、HttpServletResponse、
           ServletContext、Locale 以及包含模型数据的 Map。
         - 在需要将动态数据渲染到模板时使用
         - process 方法用于解析和渲染指定的 Thymeleaf 模板，并且生成最终的 Html 内容
         */
        WebContext webContext = getWebContext(request, response, model);

        //解析静态资源内的 goodsList.html 文件
        html = thymeleafViewResolver.getTemplateEngine().process("goodsList", webContext);

        //如果存在静态资源,则进行缓存
        if (StringUtils.hasText(html)) {
            //每过 60s 就去更新一次 Redis 页面缓存，即 60s 后该页面的缓存失效，Redis 会清除该缓存
            valueOperations.set("goodsList", html, 60, TimeUnit.SECONDS);
            log.info("Redis 缓存 goodsList 页面成功");
        }

        //返回商品的界面
        return html;
    }

    //构造 WebContext 对象
    private WebContext getWebContext(HttpServletRequest request, HttpServletResponse response, Model model) {
        return new WebContext(
                request, response,
                request.getServletContext(),
                request.getLocale(),
                model.asMap());
    }

    //基于 ThymeleafViewResolver 解析返回登录页面
    private String backToLogin(WebContext context) {
        return this.thymeleafViewResolver.getTemplateEngine().process("login", context);
    }
}
