package com.cykj.controller;

import cn.hutool.json.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.cykj.config.AliPayConfig;
import com.cykj.pojo.AliPay;
import com.cykj.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * 支付宝支付控制器
 * 
 * @author 夏日花店
 * @CreateTime 2025-11-08
 */
@RestController
@RequestMapping("/alipay")
public class AliPayController {

    // 支付宝网关地址（沙箱环境）
    private static final String GATEWAY_URL = "https://openapi-sandbox.dl.alipaydev.com/gateway.do";
    // 生产环境使用：private static final String GATEWAY_URL = "https://openapi.alipay.com/gateway.do";
    
    private static final String FORMAT = "JSON";
    private static final String CHARSET = "UTF-8";
    private static final String SIGN_TYPE = "RSA2";

    @Resource
    private AliPayConfig aliPayConfig;
    
    @Autowired
    private OrderService orderService;

    /**
     * 支付宝支付接口
     * 接收支付请求，调用支付宝支付
     */
    @PostMapping("/pay")
    public void pay(@RequestBody AliPay aliPay, HttpServletResponse httpResponse) throws Exception {
        try {
            System.out.println("=== 支付宝支付请求开始 ===");
            
            // 验证配置
            if (aliPayConfig.getAppId() == null || aliPayConfig.getAppPrivateKey() == null || aliPayConfig.getAlipayPublicKey() == null) {
                throw new RuntimeException("支付宝配置未正确初始化");
            }

            // 添加详细的配置信息输出
            System.out.println("appId: " + aliPayConfig.getAppId());
            System.out.println("私钥长度: " + aliPayConfig.getAppPrivateKey().length());
            System.out.println("公钥长度: " + aliPayConfig.getAlipayPublicKey().length());
            System.out.println("网关URL: " + GATEWAY_URL);

            System.out.println("支付参数 - 商品描述: " + aliPay.getSubject() +
                    ", 订单号: " + aliPay.getTraceNo() +
                    ", 金额: " + aliPay.getTotalAmount());

            // 1. 创建Client - 确保参数正确
            AlipayClient alipayClient = new DefaultAlipayClient(
                    GATEWAY_URL,
                    aliPayConfig.getAppId(),
                    aliPayConfig.getAppPrivateKey(),
                    FORMAT,
                    CHARSET,
                    aliPayConfig.getAlipayPublicKey(),
                    SIGN_TYPE
            );

            // 2. 创建Request
            AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
            request.setNotifyUrl(aliPayConfig.getNotifyUrl());
            request.setReturnUrl(aliPayConfig.getReturnUrl());

            System.out.println("通知地址: " + aliPayConfig.getNotifyUrl());
            System.out.println("返回地址: " + aliPayConfig.getReturnUrl());

            // 3. 设置业务参数 - 确保金额格式正确
            JSONObject bizContent = new JSONObject();
            bizContent.set("out_trade_no", aliPay.getTraceNo());
            // 确保金额格式为字符串且保留两位小数
            bizContent.set("total_amount", String.format("%.2f", aliPay.getTotalAmount()));
            bizContent.set("subject", aliPay.getSubject());
            bizContent.set("product_code", "FAST_INSTANT_TRADE_PAY");

            String bizContentStr = bizContent.toString();
            request.setBizContent(bizContentStr);
            System.out.println("业务参数内容: " + bizContentStr);

            // 4. 执行请求
            String form = alipayClient.pageExecute(request).getBody();
            System.out.println("支付宝返回表单成功，长度: " + form.length());

            // 检查返回结果
            if (form.contains("Invalid-signature") || form.contains("验签出错")) {
                throw new AlipayApiException("支付宝返回签名错误: " + form);
            }

            // 5. 返回结果
            httpResponse.setContentType("text/html;charset=" + CHARSET);
            httpResponse.getWriter().write(form);
            httpResponse.getWriter().flush();
            httpResponse.getWriter().close();

            System.out.println("=== 支付宝支付请求完成 ===");

        } catch (AlipayApiException e) {
            System.err.println("支付宝API异常: " + e.getMessage());
            System.err.println("ErrCode: " + e.getErrCode());
            System.err.println("ErrMsg: " + e.getErrMsg());
            e.printStackTrace();

            httpResponse.setStatus(500);
            httpResponse.setContentType("application/json;charset=" + CHARSET);
            httpResponse.getWriter().write("{\"error\":\"支付宝接口调用失败: " + e.getMessage() + "\"}");

        } catch (Exception e) {
            System.err.println("支付处理异常: " + e.getMessage());
            e.printStackTrace();

            httpResponse.setStatus(500);
            httpResponse.setContentType("application/json;charset=" + CHARSET);
            httpResponse.getWriter().write("{\"error\":\"支付处理失败: " + e.getMessage() + "\"}");
        }
    }

    /**
     * 支付宝异步回调接口
     * 支付宝会在支付成功后调用此接口
     */
    @PostMapping("/notify")
    public String payNotify(HttpServletRequest request) throws Exception {
        System.out.println("=== 支付宝异步回调开始 ===");
        
        try {
            Map<String, String> params = new HashMap<>();
            Map<String, String[]> requestParams = request.getParameterMap();
            
            for (String name : requestParams.keySet()) {
                String[] values = requestParams.get(name);
                String valueStr = "";
                for (int i = 0; i < values.length; i++) {
                    valueStr = (i == values.length - 1) ? valueStr + values[i] : valueStr + values[i] + ",";
                }
                params.put(name, valueStr);
            }

            // 输出所有回调参数用于调试
            System.out.println("回调参数: " + params);

            String tradeStatus = params.get("trade_status");
            System.out.println("交易状态: " + tradeStatus);

            if ("TRADE_SUCCESS".equals(tradeStatus)) {
                System.out.println("=========支付宝支付成功回调========");
                
                // 验证签名
                boolean signVerified = AlipaySignature.rsaCheckV1(
                        params,
                        aliPayConfig.getAlipayPublicKey(),
                        CHARSET,
                        SIGN_TYPE
                );

                if (signVerified) {
                    System.out.println("✅ 验签通过");
                    System.out.println("交易名称: " + params.get("subject"));
                    System.out.println("支付宝交易号: " + params.get("trade_no"));
                    System.out.println("商户订单号: " + params.get("out_trade_no"));
                    System.out.println("交易金额: " + params.get("total_amount"));
                    System.out.println("买家ID: " + params.get("buyer_id"));

                    // 更新订单状态为已支付
                    String traceNo = params.get("out_trade_no");
                    boolean success = orderService.updateOrderStatusByTraceNo(traceNo, 1L);
                    System.out.println("更新订单状态: " + (success ? "成功" : "失败"));

                    return "success";
                } else {
                    System.out.println("❌ 验签失败");
                    return "failure";
                }
            } else {
                System.out.println("交易未成功，状态: " + tradeStatus);
                return "success"; // 仍然返回success，避免支付宝重复通知
            }

        } catch (Exception e) {
            System.err.println("回调处理异常: " + e.getMessage());
            e.printStackTrace();
            return "failure";
        }
    }
}
