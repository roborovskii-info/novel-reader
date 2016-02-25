[TOC]

# アプリタイトル

小説リーダー

ePubライブラリ(Java) : https://github.com/psiegman/epublib

```java
package nl.siegmann.epublib.examples;

import java.io.InputStream;
import java.io.FileOutputStream;

import nl.siegmann.epublib.domain.Author;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Metadata;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.domain.TOCReference;

import nl.siegmann.epublib.epub.EpubWriter;

public class Translator {
  private static InputStream getResource( String path ) {
    return Translator.class.getResourceAsStream( path );
  }

  private static Resource getResource( String path, String href ) {
    return new Resource( getResource( path ), href );
  }

  public static void main(String[] args) {
    try {
      // Create new Book
      Book book = new Book();
      Metadata metadata = book.getMetadata();

      // Set the title
      metadata.addTitle("Epublib test book 1");

      // Add an Author
      metadata.addAuthor(new Author("Joe", "Tester"));

      // Set cover image
      book.setCoverImage(
        getResource("/book1/test_cover.png", "cover.png") );

      // Add Chapter 1
      book.addSection("Introduction",
        getResource("/book1/chapter1.html", "chapter1.html") );

      // Add css file
      book.getResources().add(
        getResource("/book1/book1.css", "book1.css") );

      // Add Chapter 2
      TOCReference chapter2 = book.addSection( "Second Chapter",
        getResource("/book1/chapter2.html", "chapter2.html") );

      // Add image used by Chapter 2
      book.getResources().add(
        getResource("/book1/flowers_320x240.jpg", "flowers.jpg"));

      // Add Chapter2, Section 1
      book.addSection(chapter2, "Chapter 2, section 1",
        getResource("/book1/chapter2_1.html", "chapter2_1.html"));

      // Add Chapter 3
      book.addSection("Conclusion",
        getResource("/book1/chapter3.html", "chapter3.html"));

      // Create EpubWriter
      EpubWriter epubWriter = new EpubWriter();

      // Write the Book as Epub
      epubWriter.write(book, new FileOutputStream("test1_book1.epub"));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
```

# マーケット紹介文

```
このアプリは、完全無料で「FC2小説」を読むための"非公式"アプリです。
胸がキュンキュンする恋愛小説や涙が止まらない泣ける感動小説、とても怖いホラー・ミステリー、お腹がよじれるコメディなど、「FC2小説」で公開されているさまざまなジャンルの小説を無料で読むことができます。
ガラケー・フィーチャーフォン時代に流行ったケータイ小説を、気軽にスマホでも楽しもう♪

◆ ケータイ小説に特化したビューワー
繰り返される改行、短い表現など、ケータイ小説の世界をそのままスマホで楽しめます。あえて縦書きには対応していません。

◆ 圏外でも安心、オフライン対応
小説のデータは、端末内に保存されますので圏外でも安心して読むことができます。
3G回線を持たないタブレットや古い機種でも読むのに最適。

[注意]
※このアプリは、ファイルサイズが大きな小説のデータを取得するためパケット通信料金がかかります。
※このアプリには、小説を書くための機能はありません。FC2小説のサイトより執筆をお願いします。

```

# API仕様

## getNovelList

小説一覧を取得します。

** URL例 **
http://novel-test.fc2.com/api/getNovelList.php?kind=fame

** 引数 **

* int kensu 1ページあたりの件数。省略した場合は、30件
* boolean adult アダルト設定。0:一般 1:アダルト。2:両方 省略した場合は、2。
* int page ページ。省略した場合は1。
* string kind 取得するリスト内容。省略した場合は by_id。
	by_id 小説IDで検索
	by_author 著者IDで検索
	by_genre ジャンルIDで検索
	new 新着リスト
	fame 殿堂入りリスト
	rank_gnrl 総合ランキング
	rank_day 日間ランキング
	rank_wk 週間ランキング
	rank_rtn 評価ランキング
	rank_bm しおり数ランキング
	rank_opnn 感想数ランキング
* int id 小説ID(by_id時)、または著者ID(by_author時)、またはジャンルID(by_genre、rank_*時)

** 結果 **

xmlで返ります。
ランキング以外は、最終更新日の降順(新しいほど上)に並びます。

```xml
<response>
	<response_status>OK</response_status> 通常時:OK 引数エラー: invalid argument
	<page_number>1</page_number> ページ
	<adult>0</adult> アダルトフラグ(検索条件の) 0:一般 1:アダルト 2:両方
	<all_count>14325</all_count> 総件数(ページ外含めた全て)
	<bookdatas>
		<bookdata>
			<id>46519</id> 小説ID
			<title>シンクロ白黒ブラザーズ</title> タイトル
			<genre>短編/SS,青春</genre> ジャンル
			<author_id>5280325</author_id> 著者ID
			<author_name>万城哉鬼</author_name> 著者名
			<last_mod>2009-10-09 15:17:18</last_mod> 最終更新日
			<status>1</status> 状態  0:執筆中 1:完結
			<page>2</page> ページ数
			<view>0</view> 表示回数
			<review_cnt>3</review_cnt> 評価件数
			<review_avg>3.67</review_avg> 評価平均
			<adult>0</adult> アダルトフラグ 0:一般 1:アダルト
			<caption> 概要
			3なしSS、第11弾。<br />そろそろ食べ物シリーズと改名しようか悩みます。<br />いつも通りくだらない、とりとめもない空想の産物。<br /><br /><br /><br />オチもへったくれもないですが、読んでいただければ<br />嬉しいです。感想・レビューなどいただけるともっと<br />嬉しいです。辛口批評、アドバイス大歓迎です。
			</caption>
			<top_img> 表紙イメージ
			http://novel-test.fc2.com/nimg/033/3498366/46519-00000.png
			</top_img>
			<top_img_mobile> 表紙イメージモバイル用
			http://novel-test.fc2.com/nimg/033/3498366/46519-00000-m.jpg
			</top_img_mobile>
			<top_img_thumb> 表紙イメージサムネイル(一覧リスト用)
			http://novel-test.fc2.com/nimg/033/3498366/46519-00000-s.jpg
			</top_img_thumb>
			<dl_url> DLするためのURL
			http://novel-test.fc2.com/api/getNovelContent.php?id=46519
			</dl_url>
		</bookdata>
		<bookdata>
		......
		</bookdata>
		....
	</bookdatas>
</response>
```

## getAuthorList

著者一覧を取得します。

** URL例 **

http://novel-test.fc2.com/api/getAuthorList.php?id=10&kind=by_genre&other_genre=1

http://novel.fc2.com/api/getAuthorList.php?id=12691627&kind=by_author


** 引数 **

* int kensu 1ページあたりの件数。省略した場合は、30件
* boolean adult アダルト設定。0:一般 1:アダルト。2:両方 省略した場合は、2。
* int page ページ。省略した場合は1。
* string kind 取得するリスト内容。省略した場合は by_genre。
	by_author 著者IDで検索
	by_genre ジャンルIDで検索
* int id 著者ID(by_author時)、またはジャンルID(by_genre時)
* boolean other_genre 0:小説リストに他ジャンル小説を載せない。1:他ジャンル小説も載せる。省略した場合は1。

** 結果 **

xmlで返ります。
会員IDの降順(新しい会員ほど上)に並びます。小説データは最終更新日降順(新しい小説ほど上)に並びます。

```xml
<response>
	<response_status>OK</response_status> 通常時:OK 引数エラー: invalid argument
	<page_number>1</page_number> ページ
	<adult>2</adult> アダルトフラグ(検索条件の) 0:一般 1:アダルト 2:両方
	<all_count>103</all_count> 総件数(ページ外含めた全て)
	<authordatas>
		<authordata>
		<author_id>31921</author_id> 著者ID
		<author_name>Mook</author_name>
		<author_img>http://novel-test.fc2.com/nimg/084/000516/user516.jpg</author_img> 著者イメージ
		<author_img_mobile>http://novel-test.fc2.com/nimg/084/000516/user516m.jpg</author_img_mobile> 著者イメージモバイル用
		<author_img_thumb>http://novel-test.fc2.com/nimg/084/000516/user516s.jpg</author_img_thumb> 著者イメージサムネイル(一覧リスト用)
		<bookdatas>
			<bookdata> bookdataは、getNovelListと同じ構造です。
				<id>17425</id>
				<title>電脳工程師　台湾編</title>
				<genre>歴史</genre>
				<author_id>31921</author_id>
				<author_name>Mook</author_name>
				<last_mod>2009-02-12 20:46:25</last_mod>
				<status>1</status>
				<page>22</page>
				<view>0</view>
				<review_cnt>2</review_cnt>
				<review_avg>3.50</review_avg>
				<adult>0</adult>
				<caption>台湾編</caption>
				<top_img>
				http://novel-test.fc2.com/nimg/027/031921/17425-00000.gif
				</top_img>
				<top_img_mobile>
				http://novel-test.fc2.com/nimg/027/031921/17425-00000-s.gif
				</top_img_mobile>
				<top_img_thumb>
				http://novel-test.fc2.com/nimg/027/031921/17425-00000-s.gif
				</top_img_thumb>
				<dl_url>
				http://novel-test.fc2.com/api/getNovelContent.php?id=17425
				</dl_url>
			</bookdata>
			<bookdata>
			.......
			</bookdata>
		</bookdatas>
		</authordata>
		<authordata>
		........
		</authordata>
	</authordatas>
<response>
```

## getBookMarkList

しおり一覧を取得します。

** URL例 **
http://novel-test.fc2.com/api/getBookMarkList.php?uid=87&cc=b39b78b203f712cd86facbe0aeaacac420265e80

** 引数 **

* int kensu 1ページあたりの件数。省略した場合は、全件。
* int page ページ。省略した場合は1。
* int uid FC2ユーザID
* string cc FC2ユーザIDにccpassをくっつけてsha1したもの。
例) <? php $cc = sha1 ($uid . 'novelccpass'); ?>
ccpassは別途添付します。

** 結果 **

xmlで返ります。

```xml
<response>
	<response_status>OK</response_status> 通常時:OK 引数エラー: invalid argument
	<page_number>1</page_number> ページ
	<all_count>2</all_count> 総件数(ページ外含めた全て)
	<bookmarkdatas>
		<bookmarkdata>
			<novel_id>48038</novel_id> 小説ID
			<novel_title>たらりらりん</novel_title> 小説タイトル
			<novel_genre>詩・ポエム</novel_genre> 小説ジャンル
			<page_no>1</page_no> しおりを挟んだページ
			<all_page>7</all_page> 小説総ページ数
			<regist_date>2011-02-28</regist_date> しおりをはさんだ日
		</bookmarkdata>
		<bookmarkdata>
			<novel_id>20185</novel_id>
			<novel_title>きっと俺は、俺が嫌い</novel_title>
			<novel_genre>BL/GL</novel_genre>
			<page_no>5</page_no>
			<all_page>8</all_page>
			<regist_date>2010-08-24</regist_date>
			</bookmarkdata>
	</bookmarkdatas>
</response>
```

## setBookMark

しおりをセットします。
既に同一小説にセットされているしおりがある場合、新しいしおりに置き換わります。(FC2小説Web版の仕様)

** URL例 **

http://novel-test.fc2.com/api/setBookMark.php?uid=87&cc=b39b78b203f712cd86facbe0aeaacac420265e80&novel_id=48188&page=3

** 引数 **

* int novel_id しおり挿入対象小説のID
* int page 挿入するページ
* int uid FC2ユーザID
* string cc FC2ユーザIDにccpassをくっつけてsha1したもの。

** 結果 **

xmlで返ります。

```xml
<response>
<response_status>OK</response_status> 通常時:OK 引数エラー: invalid argument 登録エラー(または該当ページなし):NG
</response>
```

## removeBookMark

しおりを削除します。

** URL例 **

http://novel-test.fc2.com/api/setBookMark.php?uid=87&cc=b39b78b203f712cd86facbe0aeaacac420265e80&novel_id=48188

** 引数 **

* int novel_id しおり削除対象小説のID
* int uid FC2ユーザID
* string cc FC2ユーザIDにccpassをくっつけてsha1したもの。

** 結果 **

xmlで返ります。

```xml
<response>
<response_status>OK</response_status> 通常時:OK 引数エラー: invalid argument 削除エラー:NG
</response>
```

* 該当するしおりや小説がない時もOKが返ります。

## getNovelContent

小説をDLします。

** URL例 **

http://novel-test.fc2.com/api/getNovelContent.php?id=46519
http://novel.fc2.com/api/getNovelContent.php?id=168027
http://novel.fc2.com/api/getNovelContent.php?id=146563
http://novel.fc2.com/api/getNovelContent.php?id=162978

** 引数 **

* int id 小説のID

** 結果 **

* 該当する小説があった場合、gzip圧縮したxmlが返ります。
* 最終更新日時が同一の圧縮済ファイルが既にある場合(ファイル名に入れた最終更新日と比べて判断)、DBを見に行かずそのままそのファイルを送信します。
最終更新日時が変わっている、または初めてgetNovelContentの対象となった小説は、DBを検索して圧縮ファイルを作成します。この時同一小説の古いファイルは削除されます。
* 圧縮ファイルは、/www/dl_novel_data に格納されています。
* 小説IDがない、削除済、非公開、ユーザ凍結の場合は、404エラーが返ります。

```xml
<response>
	<id>48188</id> 小説ID
	<chapters>
		<chapter> 章ごとにひとまとまり
			<title>醤油しか考えられない</title> 章タイトル
			<pages> 章に含まれるページの配列
				<page>
					<page_number>1</page_number> ページ
					<last_mod>2011-03-09 16:15:22</last_mod> ページ最終更新日
					<body>醤油しか考えられない。<br />なのに両親はわかってくれない。</body> 本文
					<image>http://novel-test.fc2.com/nimg/008/2785469/03357-00001.jpg</image> 挿絵
					<image_mobile>http://novel-test.fc2.com/nimg/008/2785469/03357-00001-m.jpg</image_mobile> 携帯用挿絵
					<image_thumb>http://novel-test.fc2.com/nimg/008/2785469/03357-00001-s.jpg</image_thumb>
				</page> 挿絵サムネイル
				<page>
					<page_number>2</page_number>
					<last_mod>2011-03-09 16:16:04</last_mod>
					<body>僕は家を飛び出した。</body>
					<image/>
					<image_mobile/>
					<image_thumb/>
				</page>
			</pages>
		</chapter>
		<chapter>
		.....
		</chapter>
	</chapters>
</response>
```

## getReviewList

レビュー一覧を取得します。

** URL例 **

http://novel-test.fc2.com/api/getReviewList.php?id=3357

** 引数 **

* int id 小説ID
* int kensu 1ページあたりの件数。省略した場合は、全件。
* int page ページ。省略した場合は1。

** 結果 **

* レビューデータにはidが存在しません。

```xml
<response>
	<response_status>OK</response_status>通常時:OK 引数エラー: invalid argument 小説がない/著者がいない/著者凍結中: not found
	<page_number>1</page_number> ページ
	<all_count>10</all_count> 総件数(ページ外含めた全て)
	<reviewdatas>
		<reviewdata>
			<author_id>4307365</author_id> レビュー投稿者ID
			<author_name>kie</author_name> レビュー投稿者名
			<stars>5</stars> 評価(0～5)
			<comment> コメント
			独特の世界観と　文章力が　スバラシイ！<br />オウリ、ロウ、ネイサ、イクーユ、好きです！<br />一気に読んでしまいました！読み応えがあった！<br />面白かった！！！次回作　楽しみにしています！！<br />
			</comment>
			<make_date>2009-03-25 22:31:50</make_date> レビュー日時
		</reviewdata>
		<reviewdata>
			.....
		</reviewdata>
	<reviewdatas>
</response>
```

## getIsReviewPostable

レビューを投稿可能かどうか取得します。(レビュー投稿ボタンの表示非表示制御に使います)

** 引数 **

* int novel_id レビュー対象小説のID
* int uid FC2ユーザID
* string cc FC2ユーザIDにccpassをくっつけてsha1したもの。

** URL例 **

http://novel-test.fc2.com/api/getIsReviewPostable.php?novel_id=48188&uid=87&cc=b39b78b203f712cd86facbe0aeaacac420265e80

** 結果 **

* 自分の小説には投稿できません。
* 既に投稿済の小説には投稿できません。

```xml
<response>
<response_status>OK</response_status>通常時:OK、引数エラー: invalid argument、小説がない/著者がいない/著者凍結中: not found、自分の小説: self post、投稿済: posted
</response>
```

## setReview

レビューを投稿します。

** URL例 **

例はGETですが、投稿方式はPOSTを想定しています
http://novel-test.fc2.com/api/setReview.php?novel_id=48188&uid=87&cc=b39b78b203f712cd86facbe0aeaacac420265e80&stars=3&comment=testtest

** 引数 **

* int novel_id レビュー対象小説のID
* int uid FC2ユーザID
* string cc FC2ユーザIDにccpassをくっつけてsha1したもの。
* int stars 評価0～5
* string comment 評価コメント。コメントが空でも投稿できます。(FC2小説Web版の仕様) 文字コードはUTF8、投稿できるコメントは300文字までです。

** 結果 **

* 自分の小説には投稿できません。
* 既に投稿済の小説には投稿できません。

```xml
<response>
<response_status>OK</response_status>通常時:OK、引数エラー: invalid argument、小説がない/著者がいない/著者凍結中: not found、自分の小説: self post、投稿済: posted、文字数エラー: invalid comment :too long
</response>
```

## getCommentList

感想一覧を取得します。

** URL例 **

http://novel-test.fc2.com/api/getCommentList.php?id=48080

** 引数 **

* int id 小説ID
* int kensu 1ページあたりの件数。省略した場合は、全件。
* int page ページ。省略した場合は1。

** 結果 **

* 現在、FC2小説Web版では、is_selfは自分の感想を新着情報として表示しない為に使用しています。一覧表示時は、特に区別はしていません。

```xml
<response>
	<response_status>OK</response_status>通常時:OK 引数エラー: invalid argument 小説がない/著者がいない/著者凍結中: not found
	<page_number>1</page_number>
	<all_count>3</all_count>
	<commentdatas>
		<commentdata>
			<id>22618</id> コメントID
			<author_name>作者です</author_name> コメント投稿者名
			<comment>ありがとうございます。</comment> コメント
			<is_self>1</is_self> 小説の著者による投稿か 0: 一般の人の投稿 1: 小説の著者からの投稿
			<make_date>2012-09-02 23:29:02</make_date>
		</commentdata>
		<commentdata>
			<id>22617</id>
			<author_name>ごごご</author_name>
			<comment>5ページ目の感想</comment>
			<is_self>0</is_self>
			<make_date>2012-09-02 23:26:27</make_date>
		</commentdata>
		<commentdata>
			.....
		</commentdata>
	</commentdatas>
</response>
```

## setComment

感想を投稿します。

** URL例 **

例はGETですが、投稿方式はPOSTを想定しています
http://novel-test.fc2.com/api/setComment.php?novel_id=48080&ip=218.230.83.20&comment=testtest&name=tester

** 引数 **

* int novel_id レビュー対象小説のID
* int uid FC2ユーザID。省略可能です。小説著者自身の投稿であるかどうか(is_self)のフラグ立てだけの為に使います。
* string cc * FC2ユーザIDにccpassをくっつけてsha1したもの。uidを指定した時のみ必要です。
* string name 感想投稿者名。文字コードはUTF8、文字数は10文字までです。
* string comment 感想コメント。文字コードはUTF8、投稿できるコメントは1000文字までです。
* string ip ユーザのリモートアドレス(REMOTE_ADDR)。DBに記録される他、連続投稿制限(60秒)に使われます。

** 結果 **

```xml
<response>
<response_status>OK</response_status>通常時:OK、引数エラー: invalid argument、小説がない/著者がいない/著者凍結中: not found、名前がない: invalid name :nothing、名前が長い: invalid name :too long、コメントがない: invalid comment :nothing、コメントが長い: invalid comment :too long、コメントにスパム文字列が含まれている: invalid comment :spam、連続投稿エラー: invalid comment :wait 60sec
</response>
```

現在のFC2小説のジャンル設定値
fc2novel_genre.xml　を添付します。
id がジャンルID、sort_noがソート順で、昇順に並びます(恋愛が一番上)。
滅多にジャンル設定は変わらないとおもいますが、もし、ジャンル設定が変わった時には、下記URLで、新たなxmlファイルを取得できます。
http://novel.fc2.com/api/getGenreList.php (本番環境)
http://novel-test.fc2.com/api/getGenreList.php (テスト環境)


## getNovelListByKeyword

** 引数 **

* string sort
	* ソート項目
		* keyword キーワードの一致数(デフォルト)
		* pv_cnt 閲覧数順
		* rate 評価（★の平均）順
		* lastmod 更新日順
* string order
	* ソート順
		* asc 昇順
		* desc 降順(デフォルト)

** URL例 **
http://novel-test.fc2.com/api/getNovelListByKeyword.php?keyword=%e3%83%86%e3%82%b9%e3%83%88%20%e3%82%ad%e3%83%bc%e3%83%af%e3%83%bc%e3%83%89&genre=1&adult=0&sort=pv_cnt&order=desc