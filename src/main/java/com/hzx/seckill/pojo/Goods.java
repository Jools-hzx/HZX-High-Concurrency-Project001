package com.hzx.seckill.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.math.BigDecimal;
import lombok.Data;

/**
 * 
 * @TableName t_goods
 */
@TableName(value ="t_goods")
@Data
public class Goods implements Serializable {
    /**
     * 商品 id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 商品名称
     */
    @TableField(value = "goods_name")
    private String goodsName;

    /**
     * 商品标题
     */
    @TableField(value = "goods_title")
    private String goodsTitle;

    /**
     * 商品图片
     */
    @TableField(value = "goods_img")
    private String goodsImg;

    /**
     * 商品详情
     */
    @TableField(value = "goods_detail")
    private String goodsDetail;

    /**
     * 商品价格
     */
    @TableField(value = "goods_price")
    private BigDecimal goodsPrice;

    /**
     * 商品库存
     */
    @TableField(value = "goods_stock")
    private Integer goodsStock;

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
        Goods other = (Goods) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getGoodsName() == null ? other.getGoodsName() == null : this.getGoodsName().equals(other.getGoodsName()))
            && (this.getGoodsTitle() == null ? other.getGoodsTitle() == null : this.getGoodsTitle().equals(other.getGoodsTitle()))
            && (this.getGoodsImg() == null ? other.getGoodsImg() == null : this.getGoodsImg().equals(other.getGoodsImg()))
            && (this.getGoodsDetail() == null ? other.getGoodsDetail() == null : this.getGoodsDetail().equals(other.getGoodsDetail()))
            && (this.getGoodsPrice() == null ? other.getGoodsPrice() == null : this.getGoodsPrice().equals(other.getGoodsPrice()))
            && (this.getGoodsStock() == null ? other.getGoodsStock() == null : this.getGoodsStock().equals(other.getGoodsStock()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getGoodsName() == null) ? 0 : getGoodsName().hashCode());
        result = prime * result + ((getGoodsTitle() == null) ? 0 : getGoodsTitle().hashCode());
        result = prime * result + ((getGoodsImg() == null) ? 0 : getGoodsImg().hashCode());
        result = prime * result + ((getGoodsDetail() == null) ? 0 : getGoodsDetail().hashCode());
        result = prime * result + ((getGoodsPrice() == null) ? 0 : getGoodsPrice().hashCode());
        result = prime * result + ((getGoodsStock() == null) ? 0 : getGoodsStock().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", goodsName=").append(goodsName);
        sb.append(", goodsTitle=").append(goodsTitle);
        sb.append(", goodsImg=").append(goodsImg);
        sb.append(", goodsDetail=").append(goodsDetail);
        sb.append(", goodsPrice=").append(goodsPrice);
        sb.append(", goodsStock=").append(goodsStock);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}