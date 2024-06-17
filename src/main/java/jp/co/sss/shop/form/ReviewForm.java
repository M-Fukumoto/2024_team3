package jp.co.sss.shop.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
/**
 * レビュー入力フォーム
 *
 * @author ko teiketsu
 *
 * TIPS 入力チェックアノテーションのmessage属性に"{messages.propertiesで指定した名前}"と記述することができます。
 * 
 */
public class ReviewForm {
	/**
	 * 投稿者名
	 */
	@NotBlank
	@Size(min = 1, max = 40, message = "{text.maxsize.message}")
	private String	name;
	/**
	 * 評価
	 */
	private Integer evaluation;
	/**
	 * コメント
	 */
	@Size(min = 0, max = 400, message = "{text.maxsize.message}")
	private String comment_review;
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
	 * コメントの取得
	 * @return コメント
	 */
	public String getComment_review() {
		return comment_review;
	}
	/**
	 * コメントのセット
	 * @param comment_review コメント
	 */
	public void setComment_review(String comment_review) {
		this.comment_review = comment_review;
	}
}
	