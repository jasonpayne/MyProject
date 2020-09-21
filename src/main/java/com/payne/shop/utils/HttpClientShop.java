package com.payne.shop.utils;

import lombok.extern.log4j.Log4j;
import org.apache.commons.lang.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Log4j
public class HttpClientShop {

    String cookieval = "YLS=v=2&p=1&n=1; Y=v=1&n=832jhdoarn51v&l=p7k0d6g8d6rqqv/o&p=f24vvjp052000000&ig=00cpj&r=14q&lg=ja-JP&intl=jp; JX=NdW9Bz4uunE65Q--; F=a=Mje7PW4MvScPbKe5bN47y0tf2WQrcPwzS.WWsTL7elKhLQqz9bctTEx2cM.4X5gIi2BOHJA-&b=6eml; B=ca5na79fm4mbf&b=4&d=2oySGyRpYF0iXsc695yJExzDT3GPk7Joiy6bIor4&s=iv&i=5dym3idBdB_Lz80DMniY; XB=ca5na79fm4mbf&b=4&d=2oySGyRpYF0iXsc695yJExzDT3GPk7Joiy6bIor4&s=iv&i=5dym3idBdB_Lz80DMniY; T=z=G6lYfBGi0hfBbyrrnPcWuFAMDc3MQYxMTQyMTEyMzc-&sk=DAAGLix/TEDD0y&ks=EAALViXzi_M0mlvuqH0eQowzA--~F&kt=EAA.JCY7eHx9XqsYpuw3IMu1g--~E&ku=FAAMEUCIQCfvPkvuXAE5XJFR_oYVnMfnwp5Z4vLRCcSsMr.fGA1QwIgEWjGjdiIFkFhkvQvmI8pjrUGy9hu1GJ7POuP7S8c0c0-~B&d=dGlwAXdGRU0yRAFhAVlBRQFnAVFIUktZVEJYQ0ZPTlY0UEhGNEpaSTQ3QUhBAXNsAU56QXdOZ0UyTmpNMU5qWTFOREEtAXNjAWJpem1ncgF6egFHNmxZZkJBMko-; SSL=v=1&s=lIFmRpOE6vQiw1sJqyH1I5VE_pkhR.a07zGpwr1ysGG8VkaRQQuCBuD2PicmWO2mdz3SdPPra10rvJX5k_fg.A--&kv=0; _n=DPFnxL-4PtCUH4M4G1idsx8MuEQMPWcLNUO8kFSRYa9MTwBAZjRLGMs4GnGMVjk4IEKcOFHNMdXMF-goe3DvrId-kSnIIF7AoRc44kU_HSpoOgzf6aiXrgluxMwpMoJoytT-bpqImROtn6_ONVWw4FV7MPd1nptqZ6mV69rJwZ58MMnFVy_KWUGOX-8mHTafZMIDhaO4nvByZo-P3NnOrXRmV-SOZeXmh8HVhP6RQTgCAb9aXkX5jScSa32x2c8xMHNJKj6SHs2Oz6aHL31YxuBemfV-iIbrSB2TpmBPczVx2zLYibfPi3c4Zr2iHlmtJq6hWcX8fN_amu_XW3-ElPCxLK6W-tZoSy59CVa64Yq3y1ySfp5U47LW3QjB3SVf6rmuw7jOVIGWrC5v9yhu5uvPPcs7v6WsdyC98PPU4im6Y8ZRXdlxCn0UOoJ2DwolPPPS-u6o5daTXzfv2pLRgUjTUR_f4LEAjo3Erkfp4ptIGSe5TaQ93cerS6kkS9p57oJaelE_-9FQydvYqvKoUSEltJNfBFmyizszqruMH0ulZnVwQIxV98gW9aDgNi6ouYyoy8pvN_zXF6zz7RcuoTmnsVM8l-vDdQVa21RsrVaDioOAudt2xm8dyJQMH4hIXO1KBFy4ZbzZ8vfWt04inuv5ZQyG_xDUpmQqN4aLtLB91CccY-J22Y0VrERh4fW-1NqKJ5XblU5pYXR623Vzw3PxAwtL55Qq3bc6U2jOU4KtukNXYJXiZ1IO5gtMTT2v75AilfmM7TyyL_kIDG9uYMIED0-RYMubyCpmMy9T3279CLpDPxWCIp_ug02IyxcAfxXwn0aDeQ_bV_JJJHVX5L_mYFCQ3l6nbMFATQt_ROA5zVoQ6e4xS0yHaaFtEbuskuFCPlf2elYktrb0QI5DxRkvuBxFM7ZpCyCr39PzJgvSmQ9dTeBBWH7_Nphjr70O526CvLg0RoNl2cdEWpk-Z7S-MsPHCZP1tZbJoeUgTpiwufqWKLXs85woD2S0oi0KU1kq4jZIsEML3V3JmFbKXmJCq-5ed08MAT6Oh7EUt0b54XjGWAN4eIYhpkGuBpKob1sB0IQmZhzfVXHhcUKgaSnGaMY_LaBMGOfOutInKBH9QSfuGa89dUB8dpP35ogZ.2; XC=uC/8eX4UvOkX3q0vaplFM+OPJUazGms9XhCI8NSzkh522n3G0yfPR71cDFWKMxzqpKDtc7M3Wk1L2gWXg+NjTy9+KKUhXjiN57NHrdv084zJoeRCQx30Cbcvi5awaiBseyNFDvb2WWs7y/HeyZVRMQetkIGMi6ymIsb95ethUHG2jUetlaUgg+MjV05pLNEK2LO1Qki7ZxJHre8g5X4QQAd9UDr7peayZCq0E/W5sjh8p6qvK1PhVLZJ/4TTqFycVE2UT7TplFPr00JJ+jG8fRernlvtnrqco7jmVcsvXQQkACt9aLEtqis4F7D1KlRq0ZR/h3vfqtJButL5stS4+PBKUi1unvnlXxvVnw9NUB9GbPOA9pZYuvDEdHIbfQ6ItMD440cUN7Tm2/if0npFF5ZlGvxkRCFX+18KB2dhsXIzxf5mAQXT2897GCX/j+m9ZKuglJfP9vKCGnmzXxbmNm57j9FNvVSwTTPRQPTN/d3vKiQB3IBJgHEdeSxugt5hR8cWt9ohCRFoT/Ym8D98Uif1ZPjLq2luDSTihxZCg47ZCJNXE88ImePmqDNaFCN2HsHVSMOzNwsLIJWJRswllUllT45P7LHoquSz3zpU79M=.1; YLF=v=1&y=aytMBaIgJsYhL6PYsb76PSHLIh0.gMDGx2qnBF4Hv5KvCwhTq8gUxtCGmw--&s=yL8ThW03nstesNyYqhk_Dg76ekM-; A=bar2sm1fm4meg&t=1600281040&u=1600282246&sd=A&v=1&d=C08MKWPROjfMPcdS509Pdm4Qth9KJ4qVB+kjV2pGg+HfLbeMXMJ9rXrr8PZ7vCAz9cRaxMVk7EU9snwCS5AZKJCOppHjSQJqb20uVeznba3mxfDFPnTj7zGqVsVwktfL/Jq0D2YcuEFPm+NAMtDLe7kggWWFQ6VRJV5R1kQkrI193O/UwQMsJMNk/0; XA=bar2sm1fm4meg&t=1600281040&u=1600282246&sd=A&v=1&d=C08MKWPROjfMPcdS509Pdm4Qth9KJ4qVB+kjV2pGg+HfLbeMXMJ9rXrr8PZ7vCAz9cRaxMVk7EU9snwCS5AZKJCOppHjSQJqb20uVeznba3mxfDFPnTj7zGqVsVwktfL/Jq0D2YcuEFPm+NAMtDLe7kggWWFQ6VRJV5R1kQkrI193O/UwQMsJMNk/0";
    String cookieval4 = "YLS=v=2&p=1&n=1; Y=v=1&n=832jhdoarn51v&l=p7k0d6g8d6rqqv/o&p=f24vvjp052000000&ig=00cpj&r=14q&lg=ja-JP&intl=jp; JX=NdW9Bz4uunE65Q--; F=a=s21keh0MvSeYos6XTTyMYWxogUGJyRKK_Jw_oXUM5Dmu2TX0JGsEef4V1x6h3MX58Zu_W9c-&b=j4_c; B=ca5na79fm4mbf&b=4&d=2oySGyRpYF0iXsc695yJExzDT3GPk7Joiy6bIor4&s=iv&i=NISPafIXVLcXpY9kns_m; XB=ca5na79fm4mbf&b=4&d=2oySGyRpYF0iXsc695yJExzDT3GPk7Joiy6bIor4&s=iv&i=NISPafIXVLcXpY9kns_m; T=z=UCmYfBUq0hfBFqhNbzTroKLMDc3MQYxMTQyMTEyMzc-&sk=DAAr.BdGV7V9d3&ks=EAAxOnHJQN3FEWwAGxgrfNPdg--~F&kt=EAAidz1RHUh89HwYdfVjQhNGQ--~E&ku=FAAMEUCIFyl54m1pAtYQT1qKHklVhf4L1qCYlmYKn_OCT83pVeAAiEAonlnwPVacDN63fEi1vHiX55aROcc5ZhCSeDp4a0h_ho-~B&d=dGlwAXdGRU0yRAFhAVlBRQFnAVFIUktZVEJYQ0ZPTlY0UEhGNEpaSTQ3QUhBAXNsAU56QXdOZ0UyTmpNMU5qWTFOREEtAXNjAWJpem1ncgF6egFVQ21ZZkJBMko-; SSL=v=1&s=r6AaFOfquiex2qAOSCudzpdSqJGckhVR6Se.pneB01HCJH6pBrIb26pcKfrowLB.A81PDo27bXjpcvdc4_hy.A--&kv=0; _n=DPFnxL-4PtCUH4M4G1idsx8MuEQMPWcLNUO8kFSRYa9MTwBAZjRLGMs4GnGMVjk4IEKcOFHNMdXMF-goe3DvrId-kSnIIF7AoRc44kU_HSpoOgzf6aiXrgluxMwpMoJoytT-bpqImROtn6_ONVWw4FV7MPd1nptqZ6mV69rJwZ58MMnFVy_KWUGOX-8mHTafZMIDhaO4nvByZo-P3NnOrXRmV-SOZeXmh8HVhP6RQTgCAb9aXkX5jScSa32x2c8xMHNJKj6SHs2Oz6aHL31YxuBemfV-iIbrSB2TpmBPczVx2zLYibfPi3c4Zr2iHlmttLwcp2BA5y-ZtCIW9iA_dn0ipbKJ01SuHldSpZ8HASoAm5KiRJ2M73ebzEp_AgR2z1fn48HlfUCLVOUjupFIkCnS9KN5fQ_obpW89-7OsGaOvAgNTQUWQuytu3VzGjfe6MUWoPN3i2CCERBDB8IlPW5WugQj367Xil-1kQ8swEnQi35scVPnxusRZ1ACZ5W6VYlcms17-ITYiFmI53p8hBwTEbAeE9ti5atLmYj3y7IzvczSMX4KVez181Ox0ctmbAOX6LaeGvXtUlSjcobm9D1JXpL8OFQDZV1wPpGOZjU1paADMgURLzjy52besw_Dra9O4kZERe9jSOJNRp7V1uxYKkRdNV3P6GZUOwENGjmnsay2ZUSlTcffkPTiOidozi3x0M77rZB2pfP-Gj8IsxriGKTcZVZfwLdvEUWy5Kh2k4ytYLIK4CvFTaq4zBwr0ViRxO-crE6V9_JZLCk_dOBqCw0zWzv0swAOzw2aFaAW5n8ap006w22mv2JzU78MTeO8HIde0BjhkZwfu6iedUKHOidXRyZJCgoNb5M8P36B5p0AeYGBuGQTpwQ7fFx2VtvXDS7MeKoLs_lH_71CMNvRqCk5Y8uGDRIG9gkI76Hxntvy3O--eQDcRfOO45XCZ22xWzYFo1_yL7n83mkIZUnuhquH8C7GBmwbhksMxGJfu3Ga2Z7u14GlwYorGozqiLVqzGNob4Cu2TeI1r1CQMVowcMZ7un7oXH3wtTiWTTCyLK7RQGwdJIz2OybLMT0OhvSyaDJ3RmE25qBI5SpIvFvj_0BEpD6nYp9ViPMu8qxj4aA-dD6pwGkjSrV93Cc.2; XC=uC/8eX4UvOkX3q0vaplFM+OPJUazGms9XhCI8NSzkh522n3G0yfPR71cDFWKMxzqpKDtc7M3Wk1L2gWXg+NjTy9+KKUhXjiN57NHrdv084zJoeRCQx30Cbcvi5awaiBsiusSHGhSZpgGdKnjVTmquiJIwyRXh36C6ZF2kg/E0F84hBc2QLGnTm3JOxFXo9D06EzBPQ7+oDSczt5JjcEhemUnvdIHo3lB0EbbwU2MmWCpFxRan7KY6qHEv1owCR5becrrCpVZtqoRSrDtiU81g/dPCbmIKOobTBt3mc4K+43cdSsy6+77puyjE+dMCP6t7ogqjK7iX/V03nLFWXJT3M/hyNQ2h0Gr8eqgYM5xMhPgzPsBFVFXU5HMGQW5INv0EvSHuQ3pXGyFvlvny8WNMZgypnsw4BCBJWKYM4eVacPYwYrdfETKj07ZdYhOYJ1T/FM5IUAtkYbcTZlZabVbn9ncOdHzR+2On6jGCqL6l9ftecYGNwA/aAm62SSDiR0xipT+QqF9Yn7CgtlTHm0HGzvvSdwkSqpUptq5vpxwCrcncPNAsmBGyDotoixVFGO2J4N2ycHA3TL0SxwGxy/Atgrn6oBHZ5zcxCfoExeLnI0=.1; YLF=v=1&y=aytMBaIgJsYhKqDcsb76Pb8YmZQMEiNx0cXx5lmg1vWib.QnHPMaPOkVGA--&s=cvAqaKCBnws1YCXQgioizfZXea4-; A=bar2sm1fm4meg&t=1600281040&u=1600282772&sd=A&v=1&d=C08MKWPROjfMPcdS509Pdm4Qth9KJ4qVB+kjV2pGg+HfLbeMXMJ9rXrr8PZ7vCAz9cRaxMVk7EU9snwCS5AZKJCOppHjSQJqb20uVeznaK7ixfDFPnTj7zGqVsVwktfL/Jq0D2YcuEFPm+NAMtDLe7kggWWFQ6VRJV72gi9ZkPg/azZA8lTGkfhe/0; XA=bar2sm1fm4meg&t=1600281040&u=1600282772&sd=A&v=1&d=C08MKWPROjfMPcdS509Pdm4Qth9KJ4qVB+kjV2pGg+HfLbeMXMJ9rXrr8PZ7vCAz9cRaxMVk7EU9snwCS5AZKJCOppHjSQJqb20uVeznaK7ixfDFPnTj7zGqVsVwktfL/Jq0D2YcuEFPm+NAMtDLe7kggWWFQ6VRJV72gi9ZkPg/adasdasdasds/0";
    String cookieval6 = "B=f09upohfm5jcv&b=3&s=cv; XB=f09upohfm5jcv&b=3&s=cv";

    String cookieval5 = "YLS=v=2&p=1&n=1; Y=v=1&n=832jhdoarn51v&l=p7k0d6g8d6rqqv/o&p=f24vvjp052000000&ig=00cpj&r=14q&lg=ja-JP&intl=jp; F=a=Znj5rywMvSePKuj.MugtTL3wj.by4Dajlr5UG3yUiB5IJPpv8Pmnj3rHV2UFXZO1JbFmvq8-&b=Sr3j; B=f09upohfm5jcv&b=4&d=2oySGyRpYF0iXsc695yJExzDT3GPk7Joiy6bIor4&s=fb&i=hQTYw2W8DEP15wvhhUYQ; XB=f09upohfm5jcv&b=4&d=2oySGyRpYF0iXsc695yJExzDT3GPk7Joiy6bIor4&s=fb&i=hQTYw2W8DEP15wvhhUYQ; T=z=iMtYfBi07hfBUMeVJZ0tiAHMDc3MQYxMTQyMTEyMzc-&sk=DAAqmHHq6lgV8x&ks=EAAHCcJJS_Yt0SmtF2EyOPc5A--~F&kt=EAAbjo.QNNEhLbigVuWYVkj2A--~E&ku=FAAMEUCIG20qMHacn88B3N5rskWWU53kWyxh0UschShAFfchqTaAiEA4b_vI.w6RWVl4AH5yMh93f9NNKoUs_wE6lwZAuBtwXI-~B&d=dGlwATZVaTU5QwFhAVlBRQFnAVFIUktZVEJYQ0ZPTlY0UEhGNEpaSTQ3QUhBAXNsAU56QXdOZ0UyTmpNMU5qWTFOREEtAXNjAWJpem1ncgF6egFpTXRZZkJBMko-; SSL=v=1&s=MkNLuFyj79m3hGPhHwP5HVMFRztI7HynEAvlANkakiVk7xKYtLDuq14r3V47qQzfGWaZI11TxaPo_DreCh6NRg--&kv=0; _n=DPFnxL-4PtCUH4M4G1idsx8MuEQMPWcLNUO8kFSRYa9MTwBAZjRLGMs4GnGMVjk4IEKcOFHNMdXMF-goe3DvrId-kSnIIF7AoRc44kU_HSpoOgzf6aiXrgluxMwpMoJoytT-bpqImROtn6_ONVWw4FV7MPd1nptqZ6mV69rJwZ58MMnFVy_KWUGOX-8mHTafZMIDhaO4nvByZo-P3NnOrXRmV-SOZeXmh8HVhP6RQTgCAb9aXkX5jScSa32x2c8xMHNJKj6SHs2Oz6aHL31YxuBemfV-iIbrSB2TpmBPczVx2zLYibfPi3c4Zr2iHlmtWzVgSOWBTJHk9jiodVomozk4CtENZJF8vy-vheNBoY-WIih_ESDQa9IJ3z2PO_trQTXsVEK27v1KPMftqbqe0CXtciUQZxg-L4oeyYjPh0ivGAHC6ePlanGpEIBAPTiVOwyDqfliT7PpTftq4jJAs3TWDdajB0cYLBsG42TOPo5TRyUx8HJGWYsOLheD6PS5OBMyWSlx-p_2Lvr45csmPjZ9ls7H6pVtfheLEIIyf7oG9UF5mo527s5krLJCDSwsIxCmi1-uQg7YuUNgana9NoSbpGl05Cm98HsbYqX_e0c05sRVhX5V_kx9HF3UEB3oi9lNJsGIry9qQWTUEgjkYhgxAM7CTnPWMJtMZwFvQYx_FjSm1GBaPsUeM7kOxhl8EEJO4KarArFUUGIhDylH0fe54U_qiUpqdOS2Aa_CePoR6nMnpo-b3FrpsweB4cVp5Jxhznd6efJLccZJ0WqqlX8thkjydMP_6KKxCzct9jf6y7VDUy0Wzop9v5YC8ekR8rAQgkSe2w1Empy_8i-Q7BZTGMVTXifVK4VKGbEbGg_uhxTdeT-aoTfaiHuKL2wekmVxwgTmbiv9uKgxYL_UA4b14mc4ukHzrpQmcn_qIEeIdgc4MC6NfwuHV_8Sn6BQ_cuxVvJyTbW0mhOaIhbxB5UO91_sK7bI454SLxESoAmSxmqhxpfINTlEae2vMNyPMTd8UA6OUK_BP0ZNOApO4p5xRd9cv-xbxLdSL3WnpeCX_jBTujxl2fyfyNaWPKimiH2UEvaQ08j9cKBLoC55Fh7QlorwQg-gLz08RDB9_C2Le35V-kWJa-ou9AELKukW.2; XC=uC/8eX4UvOkX3q0vaplFM+OPJUazGms9XhCI8NSzkh522n3G0yfPR71cDFWKMxzqpKDtc7M3Wk1L2gWXg+NjTy9+KKUhXjiN57NHrdv084zJoeRCQx30Cbcvi5awaiBs+HJbxOVZIwCFYkR3WU36ePL7vZRsUK9wJhcWl0TSRJc31LBv8AY1p1C0SPu/BJbTIrJWlAZB5j83N2c6E0540fP/NSvaSy3E5ppEJrp4wXiWnLx5E/C+fkZtqJYZl8WQognNEdMfqieKSoSJZTaytB+AQxwIA8FgPcVQXEQB5rI+b2VJoizi/tD2BZ9SlbqBlznOo+lSQV1h9zlIP00ciojGGQhA2MeDoMbLGo3m7xsIvN9yaGFHZ5jQ+3z5AgYl0OevnTmMdwK1eB9kvpOeaMpEhhsZLIgnCzJbrUMb5nDuRIjB351e/hK/NM/eKrhxrcnxcnUtKYRf/2BKHfLRQxXRvBEXiFdICBf8FVhaGOMMRh6eV8N6tcGbyKhhI2s5VCmOy7P3D8m0/QCZO9kJ1ToSkq5slliR598cxME0/R+CKQ5iGhYQiNzrscnI90yazwt/jD1glHkEMDQDgrFZc0C3dvCRtz+fORODnDC0H9A=.1; A=4vg291dfm5je2&t=1600310722&u=1600312098&sd=A&v=1&d=C08MKWPROjfMPcdS509Pdm4Qth9KJ4qVB+kjV2pGg+HfLbeMXMJ9rXrr8PZ7vCAz9cRaxMVk7EU9snwCS5AZKJCOppHjSQJqb20uVOXnb6DoxfDFPnTj7zGqVsVwktfL/Jq0D2YcuEFPm+NAMtDLe7kggWWFQ6VRJV6iW8YDE7z3j5RUj2B920b2/0; XA=4vg291dfm5je2&t=1600310722&u=1600312098&sd=A&v=1&d=C08MKWPROjfMPcdS509Pdm4Qth9KJ4qVB+kjV2pGg+HfLbeMXMJ9rXrr8PZ7vCAz9cRaxMVk7EU9snwCS5AZKJCOppHjSQJqb20uVOXnb6DoxfDFPnTj7zGqVsVwktfL/Jq0D2YcuEFPm+NAMtDLe7kggWWFQ6VRJV6iW8YDE7z3j5RUj2B920b2/0; JX=NdW9Bz4uunE65Q--; YBYBIZ=id=1603038&userid=exitus2280plane&sign=HLQ0IQX2m9CbY_RBnYqDQzlzQJI4Oa7Ls0ui3paYcLkSnt7sbw8TYcci14p7AwKkSCOSqFBJaO9X78KRIqTqJtMGKmJ83u3454Orhuk1zJOgdDY10g3shBp7F24AmrHdxMhaI9xjQh0hAOriTp8JzkyAq3JKXNeoeNxTqEDcFz4-&time=1600312698&expires=20160&ip=58.37.230.189&roles=|10535.U|&persistent=0; YBIZEXT=userid=exitus2280plane&company_id=1397270&kigyou_id=471125&sign=vZESpelv4_T6M8g1yEGFnGfC6tgyxtpYCH5BYmiw5kRMmxHMxIp4xQegG88HVnG1Wy40hb2E1I0o6Jcg1CVtOPTIBkn6DrxORN1FUsKfX_WmMi.bSvY82i1WwqLiImvCOIlDZVqj0r_LOXqrQoHLAkhaxM4Tj86X2233zECk1l4-&time=1600312698; BS=tO2b00009b87fee8d6f6466ba774245092192839580161fe8e84b07c4c253cd47bfd0c4c770e206cb909948b8cec5bf0498596ce";


    public String sendGetForLogin(String url, String param, String charset) {
        String result = "";
        ByteArrayOutputStream bos = null;
        try {
            String urlNameString = url;
            if (param != null && param != "") {
                urlNameString = "?" + param;
            }
            URL realUrl = new URL(urlNameString);
            if ("https".equalsIgnoreCase(realUrl.getProtocol())) {
                SslUtils.ignoreSsl();
            }
            HttpURLConnection connection = (HttpURLConnection) realUrl.openConnection();
            // 设置通用的请求属性
            if (StringUtils.isNotBlank(cookieval5)) {
                connection.setRequestProperty("Cookie", cookieval5);
            }
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent", "Mozilla/5.0(Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/69.0.3497.92 Safari/537.36");
            connection.setUseCaches(true);
            connection.setInstanceFollowRedirects(false);
            connection.setConnectTimeout(30000);
            connection.setReadTimeout(30000);
            Map<String, List<String>> map = connection.getHeaderFields();
            String redirectUrl = connection.getHeaderField("location");
            List<String> cookies = map.get("Set-Cookie");
            String cookieQuery = "";
            for (String cookie : cookies) {
                cookieQuery = cookieQuery + cookie.split(";")[0];
            }
            URL redirectsUrl = new URL(redirectUrl);
            HttpURLConnection connectionRedirect = (HttpURLConnection) redirectsUrl.openConnection();
            connectionRedirect.setRequestProperty("Cookie", cookieQuery);
            connectionRedirect.setRequestProperty("accept", "*/*");
            connectionRedirect.setRequestProperty("connection", "Keep-Alive");
            connectionRedirect.setRequestProperty("user-agent", "Mozilla/5.0(Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/69.0.3497.92 Safari/537.36");
            connectionRedirect.setUseCaches(true);
            connectionRedirect.setInstanceFollowRedirects(false);
            connectionRedirect.setConnectTimeout(30000);
            connectionRedirect.setReadTimeout(30000);
            // 定义 BufferedReader输入流来读取URL的响应charset
            InputStream inputStream = connectionRedirect.getInputStream();
            byte[] buffer = new byte[1024];
            int len = 0;
            bos = new ByteArrayOutputStream();
            while ((len = inputStream.read(buffer)) != -1) {
                bos.write(buffer, 0, len);
            }
            byte[] getData = bos.toByteArray();
            //获得网站的二进制数据
            result = new String(getData, "euc-jp");
        } catch (Exception e) {
            log.error(url + ",GET请求异常！" + e);
            return url + "GET请求异常" + e.getMessage();
        }
        // 使用finally块来关闭输入流
        finally {
            try {
                if (bos != null) {
                    bos.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }


    public String sendPostForLogin(String url, String param, String charset) {
        PrintWriter out = null;
        ByteArrayOutputStream bos = null;
        String result = "";
        try {
            URL realUrl = new URL(url);
            if ("https".equalsIgnoreCase(realUrl.getProtocol())) {
                SslUtils.ignoreSsl();
            }
            HttpURLConnection connection = (HttpURLConnection) realUrl.openConnection();
            // 请求方式
            connection.setRequestMethod("POST");
            // 超时时间
            connection.setConnectTimeout(300000);
            // 设置是否输出
            connection.setDoOutput(true);
            // 设置是否读入
            connection.setDoInput(true);
            // 设置是否使用缓存
            connection.setUseCaches(false);
            // 设置此 HttpURLConnection 实例是否应该自动执行 HTTP 重定向
            connection.setInstanceFollowRedirects(false);
            // 设置使用标准编码格式编码参数的名-值对
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            // 连接
//            connection.connect();
            /* 4. 处理输入输出 */
            // 写入参数到请求中
            String params = ".ct=&.display=&.done=https%3A%2F%2Flogin.bizmanager.yahoo.co.jp%2Fyidlogin.php%3F.scrumb%3D0%26.done%3Dhttps%253A%252F%252Fpro.store.yahoo.co.jp%252Fpro.pp-shop&.keep=&.reg=https%3A%2F%2Faccount.edit.yahoo.co.jp%2Fregistration%3Fsrc%3Dbizmgr%26done%3Dhttps%253A%252F%252Flogin.bizmanager.yahoo.co.jp%252Fyidlogin.php%253F.scrumb%253D0%2526.done%253Dhttps%25253A%25252F%25252Fpro.store.yahoo.co.jp%25252Fpro.pp-shop&.src=bizmgr&.suppreg_skip=&.yby=&auth_lv=&card_cushion_skip=&ckey=&nolink=&nonotice=&noreg=&noar=&referrer=&t_cushion=&.albatross=dD11WDVZZkImc2s9ZnMxbnk3MGJEYWdHWWZRREJzeHpWZWE5dThJLQ%3D%3D&.requiredPsCheckBox=&.slogin=zhuangqing1005&.tries=1&ls_autocomp=&showpw_status=&u=1v556spfm75fe&inactive_pw=&send_sms_counts=1&send_mail_counts=1&version=&auth_method=pwd&sms_token=&mail_token=&masked_dest=&bcrumb_id_check=dD11WDVZZkImc2s9MkUwZ2dQSGRobVdwSlZPeGhKaFN2d2R0TUZFLQ%3D%3D&bcrumb_send_sms=dD11WDVZZkImc2s9T3d2b3RNeEdCWEd2aGJBYlZ2a2J5bkhSc1djLQ%3D%3D&bcrumb_send_mail=dD11WDVZZkImc2s9Z2tCS2Z0OUxoQzJ1bTVRd3dNVFEyU0lQSExNLQ%3D%3D&user_name=zhuangqing1005&assertionInfo=&webauthn=&fido=0&auth_list=pwd&login=zhuangqing1005&passwd=langgan112233*&code=&btnSubmit=&.persistent=y&yjbfp_items=ua%02Mozilla%2F5.0+%28Macintosh%3B+Intel+Mac+OS+X+10_15_6%29+AppleWebKit%2F537.36+%28KHTML%2C+like+Gecko%29+Chrome%2F85.0.4183.102+Safari%2F537.36%01lang%02zh-CN%01screen_height%021050%01screen_width%021680%01timezone_offset%02-480%01plugins%02Chrome+PDF+Plugin%7CChrome+PDF+Viewer%7CNative+Client%01canvas_image%02iVBORw0KGgoAAAANSUhEUgAAABAAAAAWCAYAAADJqhx8AAACS0lEQVQ4T9WUPUhbURiGH6NV1Ka0JRSk1Cs2VamgUkRwCA4uIi6iJIMQNzUZBLcQhIAIDg4uChrHEFARRINCQMgSUUFQQ6Wkxh%2BM8YYEizXXotUm5VyxIjYiduqZzvk43%2FP9vedk8I8rQ%2FinUlOpp3L%2BI0BPD8zNwc7O3WIfXcLQECwvw9TUEwE3bl7vPvv7p3R2VqimPxl0d0MoBAsLtxHE2WSCwUFYWwO%2FH%2Bx2mUgkQUtLyV2AywVmM6yuQk3NNaSvDxwOSCSEI8zMQDicpgRFAa0WrNY9DIYLjMYyJAkaG2Fg4By7PYTf%2F46xMYVIRMFoLL2bgThZLODzxbFYDpGkDzQ3P2dpCa6uDnG743i9FYyOHqg96OqqvA9YWQGD4Rc2WwBZfoXPV8T2NjidmwQCWjyeYsbH99IDUikoK4Pa2l1ise%2FU1VVhMp3i8ewSDOqZndU%2BDBA5iXn39yeorg7hcBRxfPyNo6MztrYq1CY%2BmIEARKNQUABtbQFaW%2FPUkVVVvWFy8u3jAEKqej0MD8skk1G1Ue3t5dhs2Xi9MDKSpgcishCK0wmbmxAMXuJyfUany6OwsBSr9XrMvb1pAOvr8OnT9aX5eSgvP8ft%2FkJJiURDw2uKi0V0SCb3CIcT96V8U39OziWyfMrGRoyTkws6OiqJxTLQ6VJEowkWFw%2FIytJgNn%2B8rwNhkeUzpqe%2FotFAU9N7JOmFejEe%2F8HERJDs7Ezq6wvR61%2F%2BHSCsinJJbm4WmZnqW1OX0Iii%2FCQ%2F%2Fxkaza39dvfET%2FE3H89BMh0U4H0AAAAASUVORK5CYII%3D";
            OutputStream outputStream = connection.getOutputStream();
            outputStream.write(params.getBytes());
            outputStream.flush();
            outputStream.close();
            // 从连接中读取响应信息
            String msg = "";
            int code = connection.getResponseCode();
            System.out.println(code);

//
//
////            conn.setRequestProperty("Cookie", "B=9t44af9fm77ak&b=3&s=st; XB=9t44af9fm77ak&b=3&s=st");
////            conn.setRequestProperty("Referer","https://login.yahoo.co.jp/config/login?.slogin=&.lg=jp&.intl=jp&.src=bizmgr&.done=https%3A%2F%2Flogin.bizmanager.yahoo.co.jp%2Fyidlogin.php%3F.scrumb%3D0%26.done%3Dhttps%253A%252F%252Frdsig.yahoo.co.jp%252Fbizmanager%252Flogin%252Fyid_login%252FRV%253D1%252FRU%253DaHR0cHM6Ly9wcm8uc3RvcmUueWFob28uY28uanAvcHJvLnBwLXNob3A-");
////            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:26.0) Gecko/20100101 Firefox/26.0");
////            conn.setRequestMethod("POST");
////            conn.setInstanceFollowRedirects(true);
////            conn.setConnectTimeout(1000 * 500);
////            conn.getOutputStream().write(".ct=&.display=&.done=https%3A%2F%2Flogin.bizmanager.yahoo.co.jp%2Fyidlogin.php%3F.scrumb%3D0%26.done%3Dhttps%253A%252F%252Fpro.store.yahoo.co.jp%252Fpro.pp-shop&.keep=&.reg=https%3A%2F%2Faccount.edit.yahoo.co.jp%2Fregistration%3Fsrc%3Dbizmgr%26done%3Dhttps%253A%252F%252Flogin.bizmanager.yahoo.co.jp%252Fyidlogin.php%253F.scrumb%253D0%2526.done%253Dhttps%25253A%25252F%25252Fpro.store.yahoo.co.jp%25252Fpro.pp-shop&.src=bizmgr&.suppreg_skip=&.yby=&auth_lv=&card_cushion_skip=&ckey=&nolink=&nonotice=&noreg=&noar=&referrer=&t_cushion=&.albatross=dD11WDVZZkImc2s9ZnMxbnk3MGJEYWdHWWZRREJzeHpWZWE5dThJLQ%3D%3D&.requiredPsCheckBox=&.slogin=zhuangqing1005&.tries=1&ls_autocomp=&showpw_status=&u=1v556spfm75fe&inactive_pw=&send_sms_counts=1&send_mail_counts=1&version=&auth_method=pwd&sms_token=&mail_token=&masked_dest=&bcrumb_id_check=dD11WDVZZkImc2s9MkUwZ2dQSGRobVdwSlZPeGhKaFN2d2R0TUZFLQ%3D%3D&bcrumb_send_sms=dD11WDVZZkImc2s9T3d2b3RNeEdCWEd2aGJBYlZ2a2J5bkhSc1djLQ%3D%3D&bcrumb_send_mail=dD11WDVZZkImc2s9Z2tCS2Z0OUxoQzJ1bTVRd3dNVFEyU0lQSExNLQ%3D%3D&user_name=zhuangqing1005&assertionInfo=&webauthn=&fido=0&auth_list=pwd&login=zhuangqing1005&passwd=langgan112233*&code=&btnSubmit=&.persistent=y&yjbfp_items=ua%02Mozilla%2F5.0+%28Macintosh%3B+Intel+Mac+OS+X+10_15_6%29+AppleWebKit%2F537.36+%28KHTML%2C+like+Gecko%29+Chrome%2F85.0.4183.102+Safari%2F537.36%01lang%02zh-CN%01screen_height%021050%01screen_width%021680%01timezone_offset%02-480%01plugins%02Chrome+PDF+Plugin%7CChrome+PDF+Viewer%7CNative+Client%01canvas_image%02iVBORw0KGgoAAAANSUhEUgAAABAAAAAWCAYAAADJqhx8AAACS0lEQVQ4T9WUPUhbURiGH6NV1Ka0JRSk1Cs2VamgUkRwCA4uIi6iJIMQNzUZBLcQhIAIDg4uChrHEFARRINCQMgSUUFQQ6Wkxh%2BM8YYEizXXotUm5VyxIjYiduqZzvk43%2FP9vedk8I8rQ%2FinUlOpp3L%2BI0BPD8zNwc7O3WIfXcLQECwvw9TUEwE3bl7vPvv7p3R2VqimPxl0d0MoBAsLtxHE2WSCwUFYWwO%2FH%2Bx2mUgkQUtLyV2AywVmM6yuQk3NNaSvDxwOSCSEI8zMQDicpgRFAa0WrNY9DIYLjMYyJAkaG2Fg4By7PYTf%2F46xMYVIRMFoLL2bgThZLODzxbFYDpGkDzQ3P2dpCa6uDnG743i9FYyOHqg96OqqvA9YWQGD4Rc2WwBZfoXPV8T2NjidmwQCWjyeYsbH99IDUikoK4Pa2l1ise%2FU1VVhMp3i8ewSDOqZndU%2BDBA5iXn39yeorg7hcBRxfPyNo6MztrYq1CY%2BmIEARKNQUABtbQFaW%2FPUkVVVvWFy8u3jAEKqej0MD8skk1G1Ue3t5dhs2Xi9MDKSpgcishCK0wmbmxAMXuJyfUany6OwsBSr9XrMvb1pAOvr8OnT9aX5eSgvP8ft%2FkJJiURDw2uKi0V0SCb3CIcT96V8U39OziWyfMrGRoyTkws6OiqJxTLQ6VJEowkWFw%2FIytJgNn%2B8rwNhkeUzpqe%2FotFAU9N7JOmFejEe%2F8HERJDs7Ezq6wvR61%2F%2BHSCsinJJbm4WmZnqW1OX0Iii%2FCQ%2F%2Fxkaza39dvfET%2FE3H89BMh0U4H0AAAAASUVORK5CYII%3D".getBytes("utf8"));
////            conn.getOutputStream().flush();
////            conn.getOutputStream().close();
////            System.out.println(conn.getResponseCode());
////            Map<String, List<String>> map2 = conn.getHeaderFields();
//
//            // 打开和URL之间的连接
////            HttpURLConnection connection = (HttpURLConnection) realUrl.openConnection();
//            /*connection.setRequestMethod("POST");
//            System.out.println("返回码: " + connection.getResponseCode());*/
////            connection.setRequestProperty("Cookie", "B=f09upohfm5jcv&b=3&s=cv; XB=f09upohfm5jcv&b=3&s=cv");
//            connection.setRequestProperty("accept", "*/*");
//            connection.setRequestProperty("connection", "Keep-Alive");
//            connection.setRequestProperty("user-agent", "Mozilla/5.0(Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/69.0.3497.92 Safari/537.36");
//            connection.setInstanceFollowRedirects(true);
//            // 发送POST请求必须设置如下两行
//            connection.setDoOutput(true);
//            connection.setDoInput(true);
//            connection.setConnectTimeout(30000);
//            connection.setReadTimeout(30000);
//            String redirectUrl = connection.getHeaderField("location");
//            Map<String, List<String>> map = connection.getHeaderFields();
//            List<String> cookies = map.get("Set-Cookie");
//
//            // 设置通用的请求属性
//            if (StringUtils.isNotBlank(cookieval5)) {
//                connection.setRequestProperty("Cookie", "cookieval5");
//            }
//            connection.setRequestProperty("accept", "*/*");
//            connection.setRequestProperty("connection", "Keep-Alive");
//            connection.setRequestProperty("user-agent", "Mozilla/5.0(Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/69.0.3497.92 Safari/537.36");
//            connection.setRequestProperty("origin", "https://login.yahoo.co.jp");
//
//
//            // 获取URLConnection对象对应的输出流
//            out = new PrintWriter(connection.getOutputStream());
//            // 发送请求参数
//            out.print(param);
//            // flush输出流的缓冲
//            out.flush();
            /*URL realUrl = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) realUrl.openConnection();
//            connection.setRequestMethod("POST");
            System.out.println("返回码: " + connection.getResponseCode());
//            connection.setRequestMethod("POST");
            if (cookieval4.length() != 0){
                connection.setRequestProperty("Cookie", cookieval4);
                connection.setRequestProperty("User-agent", "Mozilla/5.0 (Windows NT 5.1) AppleWebKit/535.1 (KHTML, like Gecko) Chrome/13.0.782.215 Safari/535.1");
                connection.setRequestProperty("accept-language", "zh-CN");
                connection.setConnectTimeout(5 * 1000);//5秒的链接超时
                connection.setReadTimeout(5 * 1000);//设置从主机读取数据超时（单位：毫秒）
                connection.setInstanceFollowRedirects(false);
                System.out.println(connection.getURL().toString() + " ----------------------------url");
                int code = connection.getResponseCode();
                System.out.println(connection.getURL().toString() + " ----------------------------url");
                System.out.println("aaa");
            }*/

            // 定义BufferedReader输入流来读取URL的响应
            InputStream inputStream = connection.getInputStream();
            byte[] buffer = new byte[1024];
            int len = 0;
            bos = new ByteArrayOutputStream();
            while ((len = inputStream.read(buffer)) != -1) {
                bos.write(buffer, 0, len);
            }
            byte[] getData = bos.toByteArray();
            ;     //获得网站的二进制数据
            result = new String(getData, charset);
        } catch (Exception e) {
            System.out.println("发送POST请求出现异常！" + e);
            return "发送POST请求出现异常";
        }
        // 使用finally块来关闭输出流、输入流
        finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (bos != null) {
                    bos.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return result;
    }

    /**
     * 通用get请求
     *
     * @param url
     * @param param
     * @param charset
     * @return
     */
    public String sendGet(String url, String param, String charset) {
        String result = "";
        ByteArrayOutputStream bos = null;
        try {
            String urlNameString = url;
            if (param != null && param != "") {
                urlNameString = "?" + param;
            }
            URL realUrl = new URL(urlNameString);
            if ("https".equalsIgnoreCase(realUrl.getProtocol())) {
                SslUtils.ignoreSsl();
            }
            URLConnection connection = realUrl.openConnection();
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent", "Mozilla/5.0(Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/69.0.3497.92 Safari/537.36");
            connection.setUseCaches(true);
            connection.setConnectTimeout(30000);
            connection.setReadTimeout(30000);
            connection.connect();
            // 定义 BufferedReader输入流来读取URL的响应charset
            InputStream inputStream = connection.getInputStream();
            byte[] buffer = new byte[1024];
            int len = 0;
            bos = new ByteArrayOutputStream();
            while ((len = inputStream.read(buffer)) != -1) {
                bos.write(buffer, 0, len);
            }
            byte[] getData = bos.toByteArray();
            ;     //获得网站的二进制数据
            result = new String(getData, charset);
        } catch (Exception e) {
            log.error(url + ",GET请求异常！" + e);
            return url + "GET请求异常" + e.getMessage();
        }
        // 使用finally块来关闭输入流
        finally {
            try {
                if (bos != null) {
                    bos.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * 获得字符集
     */
    public String getCharset(String siteUrl) throws Exception {
        URL url = new URL(siteUrl);
        Document doc = Jsoup.parse(url, 6 * 1000);
        Elements elements = doc.select("meta[http-equiv=Content-Type]");
        Matcher matcher = Pattern.compile("(?<=charset=)(.+)(?=\")").matcher(elements.get(0).toString());
        if (matcher.find()) {
            return matcher.group();
        }
        return "gb2312";
    }

    /**
     * 向指定 URL 发送POST方法的请求
     *
     * @param url   发送请求的 URL
     * @param param 请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @return 所代表远程资源的响应结果
     */
    public String sendPost(String url, String param, String charset) {
        PrintWriter out = null;
        ByteArrayOutputStream bos = null;
        String result = "";
        try {
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            URLConnection connection = realUrl.openConnection();
//            ((HttpURLConnection) connection).setInstanceFollowRedirects(false);
            // 设置通用的请求属性
            if (StringUtils.isNotBlank(cookieval5)) {
                connection.setRequestProperty("Cookie", cookieval5);
            }
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent", "Mozilla/5.0(Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/69.0.3497.92 Safari/537.36");
            connection.setRequestProperty("origin", "https://login.yahoo.co.jp");
            connection.setRequestProperty("referer", "https://login.yahoo.co.jp/config/login?.slogin=&.lg=jp&.intl=jp&.src=bizmgr&.done=https%3A%2F%2Flogin.bizmanager.yahoo.co.jp%2Fyidlogin.php%3F.scrumb%3D0%26.done%3Dhttps%253A%252F%252Fpro.store.yahoo.co.jp%252Fpro.pp-shop");

//            ((HttpURLConnection) connection).setInstanceFollowRedirects(false);
            // 发送POST请求必须设置如下两行
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setConnectTimeout(30000);
            connection.setReadTimeout(30000);
//            connection.connect();
//            Map headers = connection.getHeaderFields();
//            Set<String> keys = headers.keySet();
//            for( String key : keys ){
//                String val = connection.getHeaderField(key);
//            }
//            String aa = connection.getHeaderField("location");
//            String bb = connection.getHeaderField("Location");
//            URL cc = connection.getURL();

            // 获取URLConnection对象对应的输出流
            out = new PrintWriter(connection.getOutputStream());
            // 发送请求参数
//            out.print(param);
            // flush输出流的缓冲
            out.flush();
            /*URL realUrl = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) realUrl.openConnection();
//            connection.setRequestMethod("POST");
            System.out.println("返回码: " + connection.getResponseCode());
//            connection.setRequestMethod("POST");
            if (cookieval4.length() != 0){
                connection.setRequestProperty("Cookie", cookieval4);
                connection.setRequestProperty("User-agent", "Mozilla/5.0 (Windows NT 5.1) AppleWebKit/535.1 (KHTML, like Gecko) Chrome/13.0.782.215 Safari/535.1");
                connection.setRequestProperty("accept-language", "zh-CN");
                connection.setConnectTimeout(5 * 1000);//5秒的链接超时
                connection.setReadTimeout(5 * 1000);//设置从主机读取数据超时（单位：毫秒）
                connection.setInstanceFollowRedirects(false);
                System.out.println(connection.getURL().toString() + " ----------------------------url");
                int code = connection.getResponseCode();
                System.out.println(connection.getURL().toString() + " ----------------------------url");
                System.out.println("aaa");
            }*/

            // 定义BufferedReader输入流来读取URL的响应
            InputStream inputStream = connection.getInputStream();

            byte[] buffer = new byte[1024];
            int len = 0;
            bos = new ByteArrayOutputStream();
            while ((len = inputStream.read(buffer)) != -1) {
                bos.write(buffer, 0, len);
            }
            byte[] getData = bos.toByteArray();
            ;     //获得网站的二进制数据
            result = new String(getData, charset);
        } catch (Exception e) {
            System.out.println("发送POST请求出现异常！" + e);
            return "发送POST请求出现异常";
        }
        // 使用finally块来关闭输出流、输入流
        finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (bos != null) {
                    bos.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return result;
    }

    public String sendPost2(String url, String param) {
        String result = "";
        ByteArrayOutputStream bos = null;
        try {
            String urlNameString = url;
            if (param != null && param != "") {
                urlNameString = url;
            }
            URL realUrl = new URL(urlNameString);
            // 打开和URL之间的连接
            HttpURLConnection connection = (HttpURLConnection) realUrl.openConnection();
            // 设置通用的请求属性
            if (StringUtils.isNotBlank(cookieval4)) {
                connection.setRequestProperty("Cookie", cookieval4);
            }
            connection.setInstanceFollowRedirects(false);
            HttpURLConnection.setFollowRedirects(false);
            String location = connection.getHeaderField("Location");
            String location2 = connection.getHeaderField("location");
            return location;
        } catch (Exception e) {
            System.out.println("发送sendPost2请求出现异常！" + e);
            e.printStackTrace();
        }
        // 使用finally块来关闭输入流
        finally {
            try {
                if (bos != null) {
                    bos.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public String sendGetNoRedirects(String url, String param) {
        String result = "";
        ByteArrayOutputStream bos = null;
        try {
            String urlNameString = url;
            if (param != null && param != "") {
                urlNameString = "?" + param;
            }
            URL realUrl = new URL(urlNameString);
            // 打开和URL之间的连接
            HttpURLConnection connection = (HttpURLConnection) realUrl.openConnection();
            connection.setUseCaches(true);

            // 设置通用的请求属性
            connection.setConnectTimeout(30000);
            connection.setReadTimeout(30000);
            connection.setInstanceFollowRedirects(false);
            connection.connect();
            cookieval = connection.getHeaderField("location").split(";")[0];
            return cookieval;
        } catch (Exception e) {
            System.out.println("发送GET请求出现异常！" + e);
            e.printStackTrace();
        }
        // 使用finally块来关闭输入流
        finally {
            try {
                if (bos != null) {
                    bos.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }
}
