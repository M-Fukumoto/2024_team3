package jp.co.sss.shop.controller.client.basket;

public class BasketItem {
	// アイテムID
	private Integer itemId;
	// 個数
	private Integer quantity = 1;
	
	public BasketItem() {}
	
	public BasketItem(Integer itemId) {
		this.itemId = itemId;
	}
	
	/**
	 * @return itemId
	 */
	public Integer getItemId() {
		return itemId;
	}
	/**
	 * @param itemId セットする itemId
	 */
	public void setItemId(Integer itemId) {
		this.itemId = itemId;
	}
	/**
	 * @return quantity
	 */
	public Integer getQuantity() {
		return quantity;
	}
	/**
	 * @param quantity セットする quantity
	 */
	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}
	
	
}
