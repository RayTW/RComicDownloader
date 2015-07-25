package JDownLoadComic.parseHtml;

import java.util.ArrayList;

/**
 * 2014.10~xxxx.xx的網頁圖片解析方式
 * 
 * @author Administrator
 * 
 */
public class ParseComicJpgUrl20 {
	private String chStr;
	private int ch;
	private int p = 1;
	private int f = 50;
	private String pi = "";
	private String ni = "";
	private String c = "";
	private int ci = 0;
	private String ps = "";

	// 由外部給入的參數
	private int chs = 0;
	private int ti = 0;
	private String cs = "";

	// 解析完的結果
	private int totalPage = 0;// 總頁數
	private String pageUrl = "";// 目前要看的頁數url

	public ParseComicJpgUrl20() {
	}

	public ParseComicJpgUrl20(String ch, int chs, int ti, String cs) {
		startParse(ch, chs, ti, cs);
	}

	public ParseComicJpgUrl20(String ch, int ti, String cs) {
		startParse(ch, ti, cs);
	}

	private void init() {
		if (chStr.indexOf('-') != -1) {
			String[] chAry = chStr.split("-");
			p = Integer.parseInt(chAry[1]);
			ch = Integer.parseInt(chAry[0]);
		} else {
			if (chStr.isEmpty())
				ch = 1;
			else
				ch = Integer.parseInt(chStr);
		}
	}

	public void startParse(String ch, int ti, String cs) {
		startParse(ch, 0, ti, cs);
	}

	public void startParse(String ch, int chs, int ti, String cs) {
		this.chStr = ch;
		this.chs = chs;
		this.ti = ti;
		this.cs = cs;
		init();
		sp();
	}

	private void sp() {
		int cc = cs.length();
		for (int i = 0; i < cc / f; i++) {
			// System.out.println(ss(cs, i * f, 4, null));
			if (ss(cs, i * f, 4).equals(String.valueOf(ch))) {
				c = ss(cs, i * f, f, f);
				ci = i;
				break;
			}
		}
		if (c.isEmpty()) {
			c = ss(cs, cc - f, f);
			ch = chs;
		}
		pageUrl = si();
		// pi = String.valueOf(ci > 0 ? ss(cs, ci * f - f, 4) : ch);
		ps = ss(c, 7, 3);// 總頁數

		if (!ps.isEmpty()) {
			totalPage = Integer.parseInt(ps);
		}
	}

	/**
	 * 取得卷數或集數
	 * 
	 * @param a
	 * @param b
	 * @param c
	 * @param d
	 * @return
	 */
	private String ss(String a, int b, int c) {
		return ss(a, b, c, null);
	}

	/**
	 * 取得卷數或集數
	 * 
	 * @param a
	 * @param b
	 * @param c
	 * @param d
	 * @return
	 */
	private String ss(String a, int b, int c, Integer d) {
		String e = a.substring(b, b + c);
		return d == null ? e.replaceAll("[a-z]", "") : e;
	}

	private String si() {
		return "http://img" + ss(c, 4, 2) + ".8comic.com/" + ss(c, 6, 1) + "/"
				+ ti + "/" + ss(c, 0, 4) + "/" + nn(p) + "_"
				+ ss(c, mm(p) + 10, 3, f) + ".jpg";
	}

	private Object ge(String e) {
		// System.out.println("ge e==>" + e);
		return e;
	}

	private String nn(int n) {
		String ret = String.valueOf(n < 10 ? "00" + n : n < 100 ? "0" + n : n);
		return ret;
	}

	private int mm(int p) {
		return (((p - 1) / 10) % 10) + (((p - 1) % 10) * 3);
	};

	public int getTotalPage() {
		return totalPage;
	}

	public String getPageUrl() {
		return pageUrl;
	}

	/**
	 * 取得一集漫畫所有的圖url
	 * 
	 * @return
	 */
	public ArrayList<String> getTotalPageUrl() {
		ParseComicJpgUrl20 parseTool = new ParseComicJpgUrl20();
		ArrayList<String> list = new ArrayList<String>();
		sp();
		int totalPage = getTotalPage();

		for (int i = 0; i < totalPage; i++) {
			parseTool.startParse(ch + "-" + (i + 1), ti, cs);
			String url = parseTool.getPageUrl();
			// System.out.println(url);
			list.add(url);
		}

		return list;
	}

	/**
	 * 取得編碼過的網址 codes(亂碼字串)、頁數等等參數
	 * 
	 * @param url
	 * @return
	 */
	public static ComicData getComicData(String html) {
		String[] tmpList = html.split("(\r\n)");
		ComicData data = new ComicData();

		for (int i = 0; i < tmpList.length; i++) {
			String tmpStr = tmpList[i];
			String chsKeyWord = "var chs=";
			String tiKeyWord = "var ti=";
			String csKeyWord = "var cs=";

			int chsIndex = tmpStr.indexOf(chsKeyWord);

			if (chsIndex != -1) {
				int chsEndIndex = tmpStr.indexOf(";", chsIndex);
				if (chsEndIndex != -1) {
					String chsStr = tmpStr.substring(
							chsIndex + chsKeyWord.length(), chsEndIndex);
					chsStr = chsStr.trim();

					if (!chsStr.isEmpty()) {
						data.chs = Integer.parseInt(chsStr);
					}
				}
			}

			int tiIndex = tmpStr.indexOf(tiKeyWord);
			if (tiIndex != -1) {
				int tiEndIndex = tmpStr.indexOf(";", tiIndex);
				if (tiIndex != -1) {
					String tiStr = tmpStr.substring(
							tiIndex + tiKeyWord.length(), tiEndIndex);
					tiStr = tiStr.trim();
					if (!tiStr.isEmpty()) {
						data.ti = Integer.parseInt(tiStr);
					}
				}
			}

			int csIndex = tmpStr.indexOf(csKeyWord);
			if (csIndex != -1) {
				int csEndIndex = tmpStr.indexOf(";", csIndex);
				if (csEndIndex != -1) {
					String csStr = tmpStr.substring(
							csIndex + chsKeyWord.length(), csEndIndex);
					csStr = csStr.trim();
					data.cs = csStr;
				}
			}
		}
		// System.out.println(data.toString());
		return data;
	}

	public static class ComicData {
		public int chs;
		public int ti;
		public String cs;

		@Override
		public String toString() {
			return "chs[" + chs + "],ti[" + ti + "],cs[" + cs + "]";
		}

	}

	// /**
	// * @param args
	// */
	// public static void main(String[] args) {
	// String ch = "743";
	// int ti = 103;
	// String cs =
	// "wxo1o821038a7tgkhpavvwcpv86tq726ka9drqh8yjfdqtaq8bwxo2o82o99frrraev4c3dkbn6fagcjkmsdbp2g3nub6tqgeua8wxo3o82o9952eqqq72vn9n7ghwsgxgqv8j9wxddn5qmeyr35mpwxo4o82o9452eqqq72vn9n7ghwsgxgqv8j9wxddn5qmeyr35mpwxo5o82o94y8g7t3794ucsjjkqju2953fy4yd5ekqsfpfqwr73wxo6o82o94gqk5xpc3vj367sqwhedpttw6t7pasj2x3tkfa2crwxo7o82o95ddufgpe85bq5ptkk73j76rt5abew47eqw6j6yujwwxo8o82o95tfx6he34cx74byce2esrqhgxvyrdfhrhk8b7nr2awxo9o821035wpy9vxf53xn673askmhqkdk5w4pkrymfyy2r4rwxo10o82o95w62n67dks79ua7mguh53j63nmuvp9ykjag9bfh8jxo11o82o95sjhuwa55mqajky5v6tjvjtkw4gsyd35hbugyvrs4xo12o82o95pgddajyyjrbveyx7h99npd54df9e8gyrushjuxv6xo13o82o932jmshhxn7d2am53s3mmrt6qswwem2g7p3y6hcvbvxo14o82o9537kshh62jn5deb6797putbtpxrybmcwya7594um9xo15o82107b32sw2nk59x6djshux6hxdte9y9v557yqumjp6p5xo16o82o95e9usshmk9gvfy885v5umvuwm9h3c5gfgtywy43rhxo17o821038scn9s6ryustym4xgcxy5eccryb88axnwhstd45fxo18o82111eadeychaet749d74dr2pcej87bj9qq37rt4um2cbxo19o821036at7efc8xxn2xh6cxhbjcu5jqxv2u4mgdpa4dyh8xo20o8210259pa6aa5mm43kbbuvry4nragnsjwsx3qr93p6nj6xo21o82o968scn9s6ryustym4xgcxy5eccryb88axnwhstd45fxo22o82106cdgtej9rsdg89nuqyr7jffcdrvj9e33nuf5e45ttxo23o82116yqtfccfjwkxp7rndmv5rvct6d4cdxbch3bfsvwyyxo24o82107sx27ygtk8dnxyhe4m3at2sm8ttt9hcdq9dhpbxgkxo25o82106fmvegu5g4xq66g9ahh3quh56p8tm2nwuvdjqy2hyxo26o82o93je4ggwvhu7n9e599wa6qxgchmt9gk79eq5br7enxxo27o82o90jh8tr4b5purbgbnt64xt2uxjujfcnm3qwh56n7s7xo28o82o89qbhvw5eun4aj5af7tc3bt3aupkcbfkndbmgeuktyxo29o821123rrqkbhqeug9v4e8ctpf27whuaqx7pab4u3dbkbwxo30o82103kgwd9hbmmxrkvbmdh72xac9s3rkmmwycfkwgdu9xxo31o82102tmxdk6vsxdeguc44u6e45r28knaejp9rg3pccyrjxo32o82105ffrq382j9ykrv6qab89yh53ym434tvjverpyqp57xo33o82112c9s7qg5p6assmf9srj3xx3edg87hfd4wgdq8ks3yxo34o8211476dssmrw964mbxke8qtdagegtheqcanax4qf8m42xo35o82o923rrqkbhqeug9v4e8ctpf27whuaqx7pab4u3dbkbwxo36o82103qbhvw5eun4aj5af7tc3bt3aupkcbfkndbmgeuktyxo37o821149a9ehm3kd4xkqfujadhuxw4hypnj7bc6rxxksywuxo38o82105sykt7bpbdqyk9qd35nfvgc9trka65mxe88njvw4dxo39o821078wpv8c2d459tqadk8vttkqumfw2f3v5ma297dvh2xo40o82115au8wtbq35hmf7x8nnhpshtk85rxdxy4uf5xn6wppxo41o82113x7depnt9pw7kwvpduf9enss25eu732sm38q8gej2xo42o82105k9yjy6rmjvrkvjcve6ede48qmpc9nr78aeejx8gpxo43o82110vappt82rc4umjm95w6fy5rge67ckjj7pbxerfkauxo44o82117vm9yfbks9k9tk6vnusv4majsurgupjnj8p326b4fxo45o82106rcn7y9n6dbks8n39ed9djpfjnm2f8qmdr78vncsaxo46o82101m8qmastsje8rrkkqss77bm6gr5wsud7ey6sq7v23xo47o82105n9tvfvrfa6h8xcndtgq2t69ppe9a2yydw3puckg9xo48o821129tcw7ycqgrpqdd2njmyfmtjmgywuade38nyw2bjcxo49o82113hdqxv8ysaqf7jaba4faxb892mdarta25s95psb5dxo50o82102wskuwv5my2c9dg9wvwu9kxqxg9y9ng2334mpeuddxo51o82112aft7q7y4pcbw76dyrwvbw6x443v47cs9a25b5cddxo52o82107ehxkw7bc7htusgw36tb527hfprffk9heutby7pxyxo53o821077m6hbkbkhx8ehsxvy3cb6b7rkkp4de2v6929xhq4xo54o82195nqwerh6hdtpv5ha92tft57rynq7fshvpspmvmbkuxo55o8218476bg5j4sumfg3urj9xjvn52xug2k2572472tu88hxo56o82107e8e3h6bsxurcdg25w9ett8k5tfw8hfmgrvbmb932xo57o911582gvex28n8ggf8cc44suyw4dpt35h8dfbjc5as6wexo58o91106q5hrr24ftf8qrta7hkj7dvye99mfxfks3ahcx5ngxo59o91105ckjnkgrf9vwd8tmmekmpxmfr44mw6mvjnsxbukubxo60o91213ve7j4gg7u9xh933wvg5c5hadjbckfmhq3ku78nvmxo61o91101h7wyw9yg76m446a8wd4qs2tdb7ded3w9qjrcjm2nxo62o912134pbderm5n8f28nvx4y7fkyhfbct8rxt8aa8mn4vwxo63112115dv7yk76ugqju6nuemt6qcua6agwt842cqsxqsew3xo64112109xpeddt59qfayw25e7brydesbturbv7kf5dns3s2qxo65112105xfmaykqcdhypdbf4mamfyacnngthyww4y5dgaqmnxo66o32106ytjqy7y4ud2g7adud5689nkfeqyr2x29qdpcx3urxo67o321124hgrcrvwjtmewy7jc57er65pe9hsxvbp2hktm8h6xo68o42111mt353ua6ncx5jywkk5er3vctnm3wn5k7huymj8cno471o82o18rwqkututcfpbfhdnvvhmrn3nbkrtgjhwfhnn4xbuo472o82o17bkgv7rna874rdyw96f5nkj6dnn76xextwgcf2t7vo473o82o18apxkg7t7hxgrtn288k5wy27jq35wvnyhusacmq82o474o82o18kc8qf8w22mqp5mgc8jyex4sst5j927xxapafexn7o475o82o18usew2uwnmd7mnm79g5n5hj3s59dnj6mc7msfycsyo476o82o17u88spx892vgdj9rgs7peeetde9492r9nuj7ksrc2o477o82o177y358caudfepx7hsukpq2dny5x6xvwcs3w3jwcbuo478o82o15fpnr8gdsk7cy2hbj4ckt66yq7qdv3rh9rvumcfwso479o82o15vgerttnrtvj5uxmadyydxg9a9g56uaxerxv9f58so480o82o176vqwf4h8phgy893vn989x9fx35t8n29bqgbjx3djo481o82o17rdp7ehd848xx9jucxte653y9thapk9qcrgkdhbkvo482o82o16539y2gakyjnspeysr6x33ky332efpu8xqdknykevo483o82o18jkmuqyjkhv7vgxepe2u6agn9kkg5qxjnqcu49ssjo484o82o18wkkfww9j2fegnase63m43c7we3m3kq6xrh8rfab9o485o82o192puutterfrx47grkudaskvgs79grteufu2dkre88o486o82o19txtvnyperx2dwckxp2epq6n56qf4th4bmt75j5xro487o82o19de6wxcruyvb23ebhwfxmrsyev2dksdr6uqnrugjfo488o82o1863u5523kg65feaph7ejbsh5cy8ykpnuhqcrhvxcxo489o82o16y3qgg8r4tcm9kg5jkbv3ejxaqfdwu9jqgn6ckbvqo490o82o18wkyxkwc7p6j7nempqy54g4b62da76kkubnugjssao491o82o19ktapsxnnjadvxkt4sqt243jxq5b44tjj5aw72tu9o492o82o22yupudwdd7ft7kkdftyeha8hvg5mpxfaf6emuwcf8o493o82o19rqkp5wm2au5m6r5hwqtk7a3h5evukqa2m52b67v4o494o82o18pb3726qkuaj4337fcwvxdtfvrp2vcga4etcbaej9o495o82o19w7hqvetyvqyvgqpu9beefqed6fbuh4p5sxgcfhxvo496o82o198bgbbnd5w96xbtktu6ms5wdah2pmhq6paydypx5do497o82o19s4ghp363d9jq3bh7yyaye53683erh9cwysxd6r2uo498o82o1974g2n4h54u4u279ktaxstte795p2dggprs9gfxa2o499o82o18g69ebydebeunsrsdq5f7hj2vu38jtftbq5t4j2ato500o82o19sa9mvfquu2gnfae8dw5ukbmejnxn5bbne9mqf4hxo501o82o18s9fc2wc5tmvx6jkm7fw6vfbcakfgs3xgw54dkqyqo502o82o1969mhngp5etc4maqywwq56nkd8ger9qwn83v4eakeo503o82o18rpqu4h6ns59y3ce9q9qq794ah5cua5tm2uygfqxso504o82o19a2x6423ud8mdqdkv7duem4xvakmd9s43cwtfcjuqo505o82o18pu9p9w9g56agqh8wkaepu6vdexgdhhps8pt5t6y7o506o82o19m7daq6tet4j22fvxxpfqqehmvw2ggxwkjkkfvctno507o82o17m8d2jj9n7mb66ffwr6qdxm5vwakdhtpbpqdvwsmho508o82o19psv97myrcwbr93yjgjbh3xd8gesedya4gehbr54fo509o82o19k5fenuhs7x9p5ghgvddgv729wyxvdwgpga9qkrbeo510o82o18n25cyc9j5a536xxkqrts2rg5q2uct8c2u9bck3seo511o82o17x3wmtscpfpcxxrkwc5gnqnp28undxu47v3dajux2o512o82o18j4bg4tenywcf3suxswyg42f38b7wc3pw9fnnx9pso513o82o18ekj52gh5665xjaug28n6t5q9wj8e2sdgrwj39esao514o82o187yra3dgyjk6wetv822v654w72aer88a3pfpffgnbo515o82o18h9gxgvm5mdfk33nv43q4tuhncjr6fbcf3xsc3xu5o516o82o17uyxd5hg7kxp9qskrb644kkkxdeqcs7jghhnty6amo517o82o19mrwpuq7kr89teaq8brmgv2h2u95acep5sggsdnwwo518o82o195stbb9t9ukcbmmk36sbg8a3fhpd243xkcnb5r6quo519o82o19eeurpgnjvjwc4y9mxmngfgjc2xxqmtdtva6fuj2vo520o82o20fxhb7h7mk5w4qr4qexh9bbjrqxxhh9582af86sxno521o82o19nudmswmwq6vxsapu5uk7c767wegqx26224jf456to522o82o19uynq8ubuavjhyv7cmd3pv4fk6wkn3uudvvdh648po523o82o19grfme86hq2h87rq25ft4vdfu9gvma2jhbdmquefmo524o82o18a84tb7fme9jshjtxmf5e5nk9ub82cd97uxc6mr9co525o82o145cj42yjjyw53b7w5bt89c53h7cgt4grqpgy5yjjto526o82o20pdsgpbbq2gjtjyt4rcbueuxgxhvb97px5kdq7nj3o527o82o192et8rjh9ky29vp9m6cxvuxwqkans9sk6k6cq9d44o528o82o1894555hwcs6er8n8yjdkc6xc876p3q4wty78duat2o529o82o168hnvt7q39axpb32tr4fjgrfsssrckx36kcatmsewo530o82o19uxrt9je96r8htd72ar7st282pqab6tt63uy3nug6o531o82o179c8apw75urn9eekkeshkxjyk6mffjeaqw759msbgo532o82o1892urt88gwvt4bkdk4hxntk5f6kggu2wmy8wm643po533o82o16juhcxvmey69t2puns4anjsrjvhv9xdjxcruuntpjo534o82o17fvedgw2nrax23q2w6fcswf3qgrhw6qvja9xjda6xo535o82o198y9aap6vngbx5k558295s6tys2rs6h6uwbaxakbqo536o82o19ur8wer2g4u446egshdebdndj6557hxh8q2u7fe4fo537o82o17p65he5xrf9sbj3nvumywv6a6psfm459ttbce47y4o538o82o199w8ae65q974ywggkra9a5uu9radanydh5sysnksto539o82o1947smq64898hp2esnqx7wc9xd2t63syq9k653bxpuo540o82o19tapj7ge7g5t45bunu9dprsr2menbks6xkp6cbh7ro541o82o17pttjkc7xtwfn2epqheq4teeqn69hdcy9qbfngfwgo542o82o16ah4gg9ksvyetf8863mps926avu664rshg3qyeue5o543o82o14kxbkxdu475n32jwuqnra23j3hs3cexmcrsdspymko544o82o19abmvxqwk7rs5ed4bsdgwa64qc63dgr7h346vxrmno545o82o17j4mtcmbsby4dhdku7hb5ae3568w2kdww46td5b6to546o82o16w4wgkjeab7s4x7474pfspxj3a3tpaqbjgss9sa35o547o82o15ujr66bajrs5sn6dkvy4npsbxckbv7q7kejykrmhvo548o82o34mva73dj77jf2qkyf9majg5fcwtsa2hqjgbyu5ywao549o82o19rjnp8mqdxgasm77as2nrjp85w7gcf5um949q39tqo550o82o15q9b7sjds7x8d7ry8678baf43pfxs8dhnh2ury8pao551o82o20nj4ch9nsp5mcewu5kx3ggg42qeyf6tmc8et9syrao552o82o16g6pspd962hn3kkqq6u5jvrbgv5ttrkqkmdrqn7ypo553o82o12sym9uc27qkerpq336923espp22npgwb5795hx8kso554o82xo8kc6pqwq6e7wyugbm3x9xdqqsnvjm6ftq6veemwsuo555o82o1169pqhm3na5fcqpcrdgjhgqhckdd7meks34h9tnxvo556o82o12ybjwc2ne5v944vjfhxa8ggs3kwpt9dqwqbsjw86yo557o82o14w2c4y4ugqp8g3dqy624khp7ntxbrp3pt6x53ve6yo558o82o13uuh8byagp2w9ny2ab7sm7bbt9hv7ghvybaqsnbtdo559o82o1526mny2rn3h34bkxtsc9a39c6rtd63gfcwt8jm3sqo560o82o14fwgemvvb2t9seu8q5wh5wxwtpp2hk9rvwhg9fg9vo561o82o15knxtgqjqunmdweq5eyrjmeauqk5dcsthnr6h84qyo562o82o15sjwfby3v89qsjcapa5tktwuaj4gmve4c2errm7u6o563o82o13ghw2jpqhv936wtdfv6rnwdabcqucu9pksmkh2q2yo564o82o13snfuym5m8f9qbs2kjt43sgfh78knrrpgvxpsfp43o565o82o153fr9txhvu6wtkttdg2frtn5v7t5srvvhndmvr34so566o82o13qqtfxse75mfc5egf3rxdcagfv642hy23hypmqdvwo567o82o169kkxa4arerunj5gnaarvh6xnpx4px76476k9kd7bo568o82o13d655teepfcquhpgu5j4abejf84a4whq733bqjr5vo569o82o1069tj5uahq72raaufqnr6gheaw587p36pq8g5xpw6o570o82o13vd4qxbg4m8d5uakbmfmkg8mnp8km7duh4tdjvthxo571o82o1347uwrpre5td9k8v2mbqpr5n542k66nxvuhtwe58ao572o82o12s7cy7neny63fdysbbj5umpvs9petbhfe35wx76duo573o82o14f5g63cpw3mcbw7arn5fnjegsyqqx2wf9w7fpa4cfo574o82o15kd6gkqmhdfsdqdp75pu4a37t4ftswta5rfpnjg2xo575o82o10pjk3j28t849xm9c3n4x3yff3b4kyu27qt2nvr9g8o576o82o13e8qa7nradhanrfegcqckhnjymnwwmgacwk5kgvpqo577o82o14sng673wdf4xpsse6ccxgc2r336rdxwg8mn2ujnh3o578o82o15qjcfawb2bksb64x77dnjd6a68ervb5partv83rr8o579o82o143tg8wgfe5gqebrey9smuwmnxbkg3evu5tkpnq2txo580o82o122d4uwxmstmtyrgugsaf3wxhb4qbeha8dwnk98b6do581o82o17mxqaewyeapdjgvyxms6yg2fg3242agwnswgvksano582o82o183k876pyn8utd333x9epmvgcs5873as8h89vkrqdqo583o82o16exy7w73dkt5gg364y5vef4ynwvje3unnun775wpno584o82o17epgjj66jvymst49btuu383uu9duqv9cp2bsmjgsjo585o82o19a97kg987mekucttj4wxnxu23w9qn7y65phtew89mo586o82o19cbyxp4hcg8p258vx69v9jnp64mcvgvx343jrd3p2o587o82o19yt8a8enx8eh8xmdc5myvr56s85nxq5jncgmy64mno588o82o18cnwp59w62cyrwa4mqvrvu83nf6s2bvtnh3y43nffo589o82o19w85fsds8yptx94qr5q56kkg9r2dbpng7uqrdda7ro590o82o18a8pmcxw77qdeaqt73yhbcac4wdbwh4enfvxdu9gso591o82o197h9h4tv6q7v6y2jg7jpgkdwja6jp2dvp3pcny5d9o592o82o18ypsvkk3nhmxub3tddtfqqreqbq7m7bmgww3dxq7go593o82o19tqm99caag8rpy9nb8xxjkbd7xgwkt9g47a2ex8yko594o82o18e5upamnhgh7usrn4p24rbfsvnrejsqy3dt9b4aqto595o82o16dpfbb4tv7nad7g4k8hwxcsp9ew9ww5dcgv6wkk39o596o82o19aheve2n3j2esh6ucb75nnj27pqxgehy2ya48pv9bo597o82o16vyrqs5s549mgytcemdfqg8h8rkrrgusyqup6x9dgo598o82o21ehut4csgg8cc6399uch6b2qy2gxfqjpepghwubewo599o82o17gq3jk43ush32mm5andr8tbansf7637spuxkwa9ryo600o82o18e4j6b6tj5vvnynmfr8nvwrtf63vhm5yb9umst822o601o82o15swucysttrek4ccavcew7h7fu4vjrfsku5jvrmguko602o82o158e2enyv7efwhbr9wbbegy4ajj32777t3mpf3t55fo603o82o162bwppgrwbfyf2wa57a99eh4h4556faw9u4q3j6m6o604o82o16u9qvceqh7s4m82sk776br7y7y2cc295y253uxsqro605112o163dmve7pw9nensf8b57kyh94f9wry3gjmuu69qkfro606o82o17f5w4gn62n4vqks4ax66bwaey6qgs5xuhj3pg6qmmo607o82o1387vra7c2ua2rx6fa42m93tv2erjjm2t3sw2hvtfxo608o82o1728t7qbgbmc7e454568pqtvybfsxvv6yf8x7wkmamo609o82o17rfqe2kcsgqdqatsshkgufh7ef4mtc6v6bp39kqyuo610o82o21m9c3vba4a98g6dkd6tqrptqe52ya5tm8664wq9cdo611o82o16jhxe45hgu5x5tb79fku5fewxwunvutx42bqf23hko612o82o164eqaqm25xakxh2maunrtdk667pyhy8u2sm8g2qsjo613o82o199jvw2652wra869fqbcku644e8vjavjwfnj6bxssgo614o82o16ec2kpb7mmy3f683d6wmtgacmxjtw7q6gqrxdd2hpo615o82o17h5pfec39dtfk5jhvppkx3p368pm5d5fyrreemv5ko616o82o22kw4p9ypxef972auem9rfpspbncv57ahgw9qj2hmeo617o82o17jxvkdvw8ureyxskycamcxcp7skkw8ddhpgsfwypdo618o91o16pcxcqy9a84d2w5sfffryq4bm2pkq4um9r7vabmcao619o22o15txafd8d373pbrungfxektjv8mbx4nwpppfu5aem4o620o22o17px2bn2xj5qtuhesufudr3evh8xgv3sq4gkp5ca2co621o91o16rmgyg8epwayf6neyr8fpv6ayfh6nw98448g967mko622o91o183rggdex235xvkewceuq84cj2btce6ygah8jgd43no623o91o19eypeq644k5ugkptgpu5qy8xhqjtktuwt8rrwjse4o624o91o17sndgtyd7npudwa8wj9mrugrmjwwqt27nk4n59y66o625o91o18sbyec6g5wh8aaqw9mx2vrevw93dpyeskfjdvb9cdo626o91o19r5bnu3rb9yu8dhcb9cbt5rgfcr272cu74jks72j7o627o22o224dt4qw8fef3s4exgchhu63fcmf3cvwru5p66rnrqo628o91o18p56esh79wwyq8ydxjtxbc2f5cve8wgnh9mq2xjdvo629102o18enb46sayurds8scu6m7dgu3xhjvck2y3pd8mdftjo630o91o188pnaa4vx3en87449rd56s9tyj4smrf39sfcqhrmuo631o91o163855pus3gvrj2vchxe9aajn4vbshpmx42qae6r2po632102o16curvjd4da5rgkuj8vwarw8m9fjsfaxmes78gqgmho633o91o16b4jhaxv2pxhxh86u6mq5t5ryytbk2teqvun3saejo634122o13c7j8rfwpyp747pbcayvqg93e9ptcdrudwy8wremco635112o17jfp8htu7vqakk65h3h8gfsbfkxfjwajyehnyn8ako636112o22cgxc4ua7rmbtdh3dx27svkhtyfehqv2tbx9g8uwko637112o21s87tae2bhksr4hgbnm5u53xkk3yen4u3ntxpdbyko638102o18phkqdjmh42uw7bhckprvsm7gfm93ht5hj6jv2dxgo639102o18arcf78htmkqjttugarmpujh2d533qucy4pd5sh5no640112o19m5ap6xe9gqcr8h9pt3rpvvkxvw5xmp2vjtrfey4go641112o183jx32nwpvbvyar4ers4jsusk6smk3yjutnuwspujo642112o189pywemm7sjypc7ebe9496kgqkeesrr77axpyx9n2o643102o198ybjk7umx6926x35mkxte4ygvpt22qc94hk2p74no644102o18u36ksvqn9vtaujw2y2rpr8gx32jqwfvt2bv38dvyo645102o177veh9sayt46fgef8jbkn3wabda2j6y5a4ng7apfao646102o12jmtrph8h3hhpkna2u8rgt49k8r5v563gr4d9n6kpo647112o13axke44myuxubr7rkdccjmh76fyenf9phgj5wrv9to648102o18bck476x7w4ypvtvptyskje7f7scyqdwwmnyh5ukfo649112o18t2e4ygrkk3sudpuffctt5du7e8j8jajmer3thjrco650112o174tksa566bpfs4sjjr8s345n2tc2knv7dxhc8k69do651102o17rvq4uctjp8sdkmp5w2n9sd2e4stwxs9my95va6nbo652102o17sadcmkg3t5ady2kefy24cjutyfqu7ncej9f8qe32o653112o18apqucsmny68kf627mtmmbedv9r9xv8h2tyfntm77o654102o17jwu8vjkvb7356pcnu8ycynhmk9wp5tb8j992dccho655102o18x6q3mhjfm647mavdknpg877atanwwv4ccyvyveg2o656112o18tpcfeggktxx6x4m6vhqc7mw95sn3gu2uavs3a5wko657112o17ca2c6gwentgaj9u54rmg52cj8u899hyg85mjnxk5o658102o197atynbrs4nkuwkcswfb93mxpe6p63evq46sm4rtdo659112o19pxyaxmpnq85rhw68c77qxkv9ne6b8pwghk2c49hgo660o32o17x76pydjjagpg8ynr8ffq9kfkn2599j7n4sq85nm7o661o32o19jy2ab6ynnqt3dqtdjmbw3xdnpp5f6k8sh7tw3gf4o662o32o192cfqcar97qs2bcm8y96y32cgwq3x7famjayn96j4o663o32o19udeqdwc9y5ckwmy3d5f8qhmrqfmmw8fm2b642yn2o664o32o188berjdae6hmf3u3j3kh3qqf4fw8r7qq23c7n4fcxo665112o179qgh8sv6wn542rc9vkhergf4vadkg2gwwrga5dq2o666o32o176sg73pvpqkwssjh2tvrgdmrw8dt8wdqhswxn6csxo667o32o18xh839c7nputg9y6vp8sc3hk8qut2pruwsnffycpfo668o32o18chqxp7qg6k2yra7rxdyma64fhfjtrqengqueyxpao669o32o17yvsw39ntap3ggw2qemngcs4apbrphja26edcnq8mo670112o17r4xdtxjted5yhkaqy9eh8w796c32jc49kgtyqcubo671o32o16jrnu5pmec74qsds6ppm73bev9wwub6tb6rteq3ayo672o32o18g7fwx4dnp5kn7ugkqv99u2a4vnp73n9p5bvkfmp2o673o32o19b3wvhwudhctegkh4e2sw7fnuuqvjx73cgqr3uwpqo674o32o17xh564g2v9semptmh6erywqu3555np6ajg2m9kscyo675o42o18j3mgk9cmjg8rmu8gynjt398274k3t5ubgnwxdtyro676o42o17fupwgm52vc35cju6a6fv643dny6nw9js5xqxd2cho677o42o16gdnftcrggcatfa78xp22yb3e58cu5ujvucqbe4e9o678o32o16eay2jgjr7t8vc96ax5dytjbwxw4dddjey29ugcgwo679o42o18sgsrdyrtfg2j8byytcdxcrundeyf2syjvw2abcb7o680o32o18jhesjk2v7nj7dbbt8ths2d7u8jy5yf79n32a4y3ho681o32o19sxurgey5wwtvp6m7q95nkcnps2y7d64sx3vt6tqmo682o42o18crdpxf2cw3vpvnt69uwxqa4k8vuntnk5pstnsh7po683o42o19yw25uk7cvc7a3ytdcaupd3qmx8qhn39rmmn8ef2ko684o42o19fk5944j4q7a859as35tgf5ta5e6cy2xvehdktqs7o685o42o17mcgyama8f2mpqqyt6arx4pe74yykdjs8tu3xrpamo686o42o19pden7tmc56ahgbdsq3g9vp698j8333kftsehuempo687o32o187ttxf4e3krufsc5ux3v62ppk8rqjkd4nhegvegdqo688o42o197x4nxddtpjgsuxck5384udnesccq3u443pawnctho689o42o17c8ep6g4tnj3nyxt8eth67hmjfps8m6vbu7qa7a52o690o32o17j7ed7a52rykqjbc94be9apxwwwy75wahcu4j3jjko691o42o15sppp7cdmfnrdjbuejuxrjt74tyw9uv7722yk3u3eo692152o17aabs9jrq24kc7u93gyh5uj24vhnmy68wwn5mcqx6o693152o16qxqy62cedegs3vxhexx45gdejt2tc7m5x2kt2dvuo694152o16gjpjcxp2qxxwff7hqp9s9wb4g5j9wbgxquh4s545o695152o17dxrpgew2nd8mks9kt97kwqpdt7wqevjgycwhknm7o696152o18bak9wu838gp2tx7gvjkfwdxg8hthnupnfkcdd7xno697152o19dg4xjdkh6hyu6jywhv9hhjjd8ehsxyq8p6hearwdo698152o1845jspgcer9p9uhd2ectj23a4wq6q6s5n7855xjcmo699152o18q8mxumba3fmkufkc29n8ygcnqasmgxkvfxt242xco700152o1975qb8t8xbtuhy2m2xu8tmex4r7d9twmn2tf6fv47o701152o189rgv98tjsmu8ftsabf7ccc8hs72j6q6ajtdu53g2o702152o14gq7a2wyux3va973dm23dsxh8ae2hp66twajhuycgo703152o18mdu2eb4fuj5kwc68dmwu5tcaf6fjfdutmp4jmq7eo704152o18dffv6yrk32uw2f9k2k9vaxg73tcr7mdvavd4bjubo705o42o196vs9wjukqu5utmr6us43b3xufn9yfq7nbx8afffko706152o1433h455p5ek2u874x3s4t9uu2t7s5dq3wt59dat2ho707152o16ne3nqtsvkpseujwt5gvcapy7cvx94hebx2yvtewyo708152o15sv4fyd43pe6k6h52tfs3rmtvwcnknfsut7y8xqqao709152o18vjmhtbwhed576kfyt9s6fcf8nrfgyw359jvp86j8o710152o17b4nhje6d4ygfpfrf7b8n7wvqucqe8k7jc2qrsdavo711152o17tmh6fgkfstue6ab63985t5j28w3545kwakdfw7w5o712152o17dkqargbkvhxeeanj8g5hqxdt5sm3akpj3nqt63wro713152o18p3gper9ysycsb4d9k3mfnrvqu9cjtgk8furnbc7so714152o18ybsgk4vnf24jqk4gyyy7264xaebwy7jmdc9g6pgyo715152o17naxttd58qbd823rp2g2ymq2gybt5bdsb6pbycuavo716152o17x3ydhqd7ntv5u5ntyengwewuxa88ywg49k2gqc6xo717152o18akefd2m2chdv3hgeysafr2de5ru5aqv4gpsn5g86o718152o17h9btkw3y2wh3rrk89t477wymbsn7m55hkndhxyqgo719152o198dsqnctecg4cu85xs3md4ran7j2edeggr6kyfgwxo720152o1987j7vvrbn2dv47ya3ngm2utxkpq5c2cq2s5s5qtdo721152o18pnb8hpxe4bn9tnht65de29msmrh4b6hnb6mb2fsco722152o188jptdbwp6ft5dbke6uwhg4fm4e92jjrm59m3qynyo723152o19bng5mw3w9rktr3yfusdpjmwewqvg7eb2xp5jvge4o724152o1843pn6bbhfrgewyu2qpx5ufjmad5paaeh3dhyqmwqo725152o1522pjsk9xg2fmjr2nr77978e97pjvyahm544x3fsuo726152o19cyfv5kk3h5m457gv9wacbgw44sbnvnpvxa6c9ds4o727152o18yexhqkkxmfgktcxw5a6287e66uy9h3x324mvyrxxo728152o17cxk3rk3ay6xyryq63xtvt4nqsesq4cbn8hfk5wyho729152o16xugmbc9p5cg9jd2jrea3gejf2m99283b77mkgvkvo730152o17tqdmrmfjvkrhrpkthancqqj6nmrc2tcccmxtfk5vo731152o19fd9c5p9uufpjuwg9gy5nv9geg8dqt7uvwsahjkq3o732152o18dxq7s7sxwj9jws2haqjgcer6jbhq3kvgfeva5muuo733152o17wcg8k3natjntfbh92k36q6ye8vycdtjs4kbrhgymo734152o18ffywhe98ptrhfr4grgb6x6n7x5ay7sc6qdafmy78o735152o19vhcbxk8acgbt2e79tb2mp5smeq5ykqetn2p4tp2no736152o17kg9e9hb5wrgm3n4gtbk7p4vnmug9h4tnhdt36reto737152o17kfej9a6jub2ka4vf3kwsp2fsvh65v64tmfahk9k7o738152o17av9sbsaxfpue6ephjf2kf2u6gmsaahcfny2qqaaro739152o19bf6s5qsbhxgkhk58ugq6n2md5d7urkp3bxcvagb7o740152o19tpf6mg2vxg43xyw7wpngkc4tew9mucxyxe2dvx7eo741152o18sph33xggmy2epx6agevt9h9k3b3a78w49q6yuab7o742152o18vtdu96s3wsv4ngpb3kpee6j8nqa4g3rq5ntkhnb9o743152o18mkxwsqmcrnprtv4nbxqwp9skjsw7cuqycf72puw4";
	//
	// ParseComicJpgUrl20 com = new ParseComicJpgUrl20(ch, ti, cs);
	// System.out.println(com.getTotalPage());
	// System.out.println(com.getTotalPageUrl());
	// // eval(unescape('sp%28%29;'));
	// }

}
