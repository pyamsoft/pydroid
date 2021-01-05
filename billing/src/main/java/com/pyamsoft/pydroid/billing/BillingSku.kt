package com.pyamsoft.pydroid.billing

/**
 * Abstraction of billing SKU
 */
public interface BillingSku {

    /**
     * SKU id
     */
    public val id: String

    /**
     * SKU price
     */
    public val price: String

    /**
     * SKU name
     */
    public val title: String

    /**
     * SKU description
     */
    public val description: String

    /**
     * SKU icon
     */
    public val iconUrl: String
}