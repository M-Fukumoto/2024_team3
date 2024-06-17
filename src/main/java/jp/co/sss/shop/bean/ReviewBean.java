package jp.co.sss.shop.bean;

/**
 * レビュー機能クラス
 *
 * @author ko teiketsu
 */
public class ReviewBean {
	/**
	 * 投稿者名
	 */
	private String	name;
	/**
	 * 評価
	 */
	private Integer evaluation;
	/**
	 * コメント
	 */
	private String commentReview;
	/**
	 * 投稿者名の取得
	 * @return 投稿者名
	 */
	public String getName() {
		return name;
	}
	/**
	 * 投稿者名のセット
	 * @param name 投稿者名
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * 評価の取得
	 * @return 評価
	 */
	public Integer getEvaluation() {
		return evaluation;
	}
	/**
	 * 評価のセット
	 * @param evaluation 評価
	 */
	public void setEvaluation(Integer evaluation) {
		this.evaluation = evaluation;
	}
	/**
	 * @return commentReview
	 */
	public String getCommentReview() {
		return commentReview;
	}
	/**
	 * @param commentReview セットする commentReview
	 */
	public void setCommentReview(String commentReview) {
		this.commentReview = commentReview;
	}

}
