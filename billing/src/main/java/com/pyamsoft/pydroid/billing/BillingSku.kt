package com.pyamsoft.pydroid.billing

/** Abstraction of billing SKU */
public interface BillingSku {

  /** SKU id */
  public val id: String

  /** SKU price human readable */
  public val displayPrice: String

  /** SKU price */
  public val price: Long

  /** SKU name */
  public val title: String

  /** SKU description */
  public val description: String

  /** SKU icon */
  public val iconUrl: String
}
