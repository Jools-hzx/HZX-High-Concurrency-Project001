package com.hzx.seckill.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import lombok.Data;

/**
 * @TableName t_order
 */
@TableName(value = "t_order")
@Data
public class Order implements Serializable {
    /**
     * 订单Id-唯一标识符
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户Id
     */
    @TableField(value = "user_id")
    private Long userId;

    /**
     * 商品Id
     */
    @TableField(value = "goods_id")
    private Long goodsId;

    /**
     * 送达地址
     */
    @TableField(value = "delivery_addr_id")
    private Long deliveryAddrId;

    /**
     * 商品名称
     */
    @TableField(value = "goods_name")
    private String goodsName;

    /**
     * 商品剩余数量
     */
    @TableField(value = "goods_count")
    private Integer goodsCount;

    /**
     * 购买的商品价格
     */
    @TableField(value = "goods_price")
    private BigDecimal goodsPrice;

    /**
     * 订单渠道 1pc，2Android，3ios
     */
    @TableField(value = "order_channel")
    private Integer orderChannel;

    /**
     * 订单状态: 订单状态：0 新建未支付 1 已支付 2 已发货 3 已收货 4 已退款 5 已完成
     */
    @TableField(value = "status")
    private Integer status;

    /**
     * 商品订单创建时间
     */
    @TableField(value = "create_date")
    private Date createDate;

    /**
     * 订单支付的时间
     */
    @TableField(value = "pay_date")
    private Date payDate;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        Order other = (Order) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
                && (this.getUserId() == null ? other.getUserId() == null : this.getUserId().equals(other.getUserId()))
                && (this.getGoodsId() == null ? other.getGoodsId() == null : this.getGoodsId().equals(other.getGoodsId()))
                && (this.getDeliveryAddrId() == null ? other.getDeliveryAddrId() == null : this.getDeliveryAddrId().equals(other.getDeliveryAddrId()))
                && (this.getGoodsName() == null ? other.getGoodsName() == null : this.getGoodsName().equals(other.getGoodsName()))
                && (this.getGoodsCount() == null ? other.getGoodsCount() == null : this.getGoodsCount().equals(other.getGoodsCount()))
                && (this.getGoodsPrice() == null ? other.getGoodsPrice() == null : this.getGoodsPrice().equals(other.getGoodsPrice()))
                && (this.getOrderChannel() == null ? other.getOrderChannel() == null : this.getOrderChannel().equals(other.getOrderChannel()))
                && (this.getStatus() == null ? other.getStatus() == null : this.getStatus().equals(other.getStatus()))
                && (this.getCreateDate() == null ? other.getCreateDate() == null : this.getCreateDate().equals(other.getCreateDate()))
                && (this.getPayDate() == null ? other.getPayDate() == null : this.getPayDate().equals(other.getPayDate()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getUserId() == null) ? 0 : getUserId().hashCode());
        result = prime * result + ((getGoodsId() == null) ? 0 : getGoodsId().hashCode());
        result = prime * result + ((getDeliveryAddrId() == null) ? 0 : getDeliveryAddrId().hashCode());
        result = prime * result + ((getGoodsName() == null) ? 0 : getGoodsName().hashCode());
        result = prime * result + ((getGoodsCount() == null) ? 0 : getGoodsCount().hashCode());
        result = prime * result + ((getGoodsPrice() == null) ? 0 : getGoodsPrice().hashCode());
        result = prime * result + ((getOrderChannel() == null) ? 0 : getOrderChannel().hashCode());
        result = prime * result + ((getStatus() == null) ? 0 : getStatus().hashCode());
        result = prime * result + ((getCreateDate() == null) ? 0 : getCreateDate().hashCode());
        result = prime * result + ((getPayDate() == null) ? 0 : getPayDate().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", userId=").append(userId);
        sb.append(", goodsId=").append(goodsId);
        sb.append(", deliveryAddrId=").append(deliveryAddrId);
        sb.append(", goodsName=").append(goodsName);
        sb.append(", goodsCount=").append(goodsCount);
        sb.append(", goodsPrice=").append(goodsPrice);
        sb.append(", orderChannel=").append(orderChannel);
        sb.append(", status=").append(status);
        sb.append(", createDate=").append(createDate);
        sb.append(", payDate=").append(payDate);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}