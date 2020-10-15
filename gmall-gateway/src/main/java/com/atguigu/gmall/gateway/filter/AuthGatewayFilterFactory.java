package com.atguigu.gmall.gateway.filter;

import com.atguigu.gmall.common.utils.IpUtil;
import com.atguigu.gmall.common.utils.JwtUtils;
import com.atguigu.gmall.gateway.config.JwtProperties;
import io.jsonwebtoken.Jwt;
import lombok.Data;
import lombok.ToString;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;

@EnableConfigurationProperties(JwtProperties.class)
@Component
public class AuthGatewayFilterFactory extends AbstractGatewayFilterFactory<AuthGatewayFilterFactory.PathConfig> {


    @Autowired
    private JwtProperties properties;

    public AuthGatewayFilterFactory() {
        super(PathConfig.class);
    }

    @Override
    public GatewayFilter apply(PathConfig config) {
        return new GatewayFilter() {
            @Override
            public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
                System.out.println("这是局部过滤器，只能拦截特点服务" + config);

                //httpServletRequest  -->  ServerHttpRequest
                ServerHttpRequest request = exchange.getRequest();
                ServerHttpResponse response = exchange.getResponse();

                //1、判断当前请求的路径在不在拦截名单上，不在放行
                List<String> pathes = config.pathes;//拦截名单
                String curPath = request.getURI().getPath();//当前请求路径
                if (pathes.stream().allMatch(path -> curPath.indexOf(path) == -1)){
                    return chain.filter(exchange);
                }

                //2、获取token信息，异步header，同步cookie
                String token = request.getHeaders().getFirst("token");
                if (StringUtils.isBlank(token)){
                    MultiValueMap<String, HttpCookie> cookies = request.getCookies();
                    if (!CollectionUtils.isEmpty(cookies) && cookies.containsKey(properties.getCookieName())){
                        HttpCookie cookie = cookies.getFirst(properties.getCookieName());
                        token = cookie.getValue();
                    }
                }

                //3、判断token是否为空，为空就拦截
                if (StringUtils.isBlank(token)){
                    //重定向到登录页面
                    response.setStatusCode(HttpStatus.SEE_OTHER);
                    response.getHeaders().set(HttpHeaders.LOCATION, "http://sso.gmall.com/toLogin.html?returnUrl=" + request.getURI());
                    //请求结束
                    return response.setComplete();
                }

                try {
                    //4、解析token信息，异常拦截
                    Map<String, Object> map = JwtUtils.getInfoFromToken(token, properties.getPublicKey());

                    //5、获取载荷中ip，获取当前请求的ip，比较如果两个ip不相等拦截
                    String ip = map.get("ip").toString();
                    String curIp = IpUtil.getIpAddressAtGateway(request);
                    if (!StringUtils.equals(ip,curIp)){
                        //重定向到登录页面
                        response.setStatusCode(HttpStatus.SEE_OTHER);
                        response.getHeaders().set(HttpHeaders.LOCATION, "http://sso.gmall.com/toLogin.html?returnUrl=" + request.getURI());
                        //请求结束
                        return response.setComplete();
                    }

                    //6、把载荷信息传递给后续服务
                    request.mutate().header("userId", map.get("userId").toString()).build();
                    exchange.mutate().request(request).build();

                    //7、放行
                    return chain.filter(exchange);
                } catch (Exception e) {
                    e.printStackTrace();
                    //重定向到登录页面
                    response.setStatusCode(HttpStatus.SEE_OTHER);
                    response.getHeaders().set(HttpHeaders.LOCATION, "http://sso.gmall.com/toLogin.html?returnUrl=" + request.getURI());
                    //请求结束
                    return response.setComplete();
                }
            }
        };
    }

    @Override
    public List<String> shortcutFieldOrder() {
//        return Arrays.asList("key", "value");
        return Arrays.asList("pathes");
    }

    @Override
    public ShortcutType shortcutType() {
        return ShortcutType.GATHER_LIST;
    }

    //    @Data
//    @ToString
//    public static class keyValueConfig{
//        private String key;
//        private String value;
//    }

    @Data
    @ToString
    public static class PathConfig{
        private List<String> pathes;
    }
}
