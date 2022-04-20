## 1、java端

```java
package com.zk.web.util;
 
/**
 * AES 128bit 加密解密工具类
 * @author dufy
 */
 
import org.apache.commons.codec.binary.Base64;
 
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
 
 
public class AesEncryptUtil {
     
    //使用AES-128-CBC加密模式，key需要为16位,key和iv可以相同！
    private static String KEY = "1234567890123456";
     
    private static String IV = "1234567890123456";
     
     
    /**
     * 加密方法
     * @param data  要加密的数据
     * @param key 加密key
     * @param iv 加密iv
     * @return 加密的结果
     * @throws Exception
     */
    public static String encrypt(String data, String key, String iv) throws Exception {
        try {
 
            Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");//"算法/模式/补码方式"NoPadding PkcsPadding
            int blockSize = cipher.getBlockSize();
 
            byte[] dataBytes = data.getBytes();
            int plaintextLength = dataBytes.length;
            if (plaintextLength % blockSize != 0) {
                plaintextLength = plaintextLength + (blockSize - (plaintextLength % blockSize));
            }
 
            byte[] plaintext = new byte[plaintextLength];
            System.arraycopy(dataBytes, 0, plaintext, 0, dataBytes.length);
 
            SecretKeySpec keyspec = new SecretKeySpec(key.getBytes(), "AES");
            IvParameterSpec ivspec = new IvParameterSpec(iv.getBytes());
 
            cipher.init(Cipher.ENCRYPT_MODE, keyspec, ivspec);
            byte[] encrypted = cipher.doFinal(plaintext);
 
            return new Base64().encodeToString(encrypted);
 
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
 
    /**
     * 解密方法
     * @param data 要解密的数据
     * @param key  解密key
     * @param iv 解密iv
     * @return 解密的结果
     * @throws Exception
     */
    public static String desEncrypt(String data, String key, String iv) throws Exception {
        try {
            byte[] encrypted1 = new Base64().decode(data);
 
            Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
            SecretKeySpec keyspec = new SecretKeySpec(key.getBytes(), "AES");
            IvParameterSpec ivspec = new IvParameterSpec(iv.getBytes());
 
            cipher.init(Cipher.DECRYPT_MODE, keyspec, ivspec);
 
            byte[] original = cipher.doFinal(encrypted1);
            String originalString = new String(original);
            return originalString;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
     
    /**
     * 使用默认的key和iv加密
     * @param data
     * @return
     * @throws Exception
     */
    public static String encrypt(String data) throws Exception {
        return encrypt(data, KEY, IV);
    }
     
    /**
     * 使用默认的key和iv解密
     * @param data
     * @return
     * @throws Exception
     */
    public static String desEncrypt(String data) throws Exception {
        return desEncrypt(data, KEY, IV);
    }
     
     
     
    /**
    * 测试
    */
    public static void main(String args[]) throws Exception {
 
        String test1 = "sa";
        String test =new String(test1.getBytes(),"UTF-8");
        String data = null;
        String key =  KEY;
        String iv = IV;
        // /g2wzfqvMOeazgtsUVbq1kmJawROa6mcRAzwG1/GeJ4=
        data = encrypt(test, key, iv);
        System.out.println("数据："+test);
        System.out.println("加密："+data);
        String jiemi =desEncrypt(data, key, iv).trim();
        System.out.println("解密："+jiemi);
         
 
    }
     
}
```



## 前端

vue 引入

```vue
npm install crypto-js
```



```vue
import CryptoJS from 'crypto-js/crypto-js'

// 默认的 KEY 与 iv 如果没有给
const KEY = CryptoJS.enc.Utf8.parse("1234567890123456");
const IV = CryptoJS.enc.Utf8.parse('1234567890123456');
/**
 * AES加密 ：字符串 key iv  返回base64 
 */
export function Encrypt(word, keyStr, ivStr) {
  let key = KEY
  let iv = IV

  if (keyStr) {
    key = CryptoJS.enc.Utf8.parse(keyStr);
    iv = CryptoJS.enc.Utf8.parse(ivStr);
  }

  let srcs = CryptoJS.enc.Utf8.parse(word);
  var encrypted = CryptoJS.AES.encrypt(srcs, key, {
    iv: iv,
    mode: CryptoJS.mode.CBC,
    padding: CryptoJS.pad.ZeroPadding
  });
 // console.log("-=-=-=-", encrypted.ciphertext)
  return CryptoJS.enc.Base64.stringify(encrypted.ciphertext);

}
/**
 * AES 解密 ：字符串 key iv  返回base64 
 *
 */
export function Decrypt(word, keyStr, ivStr) {
  let key  = KEY
  let iv = IV

  if (keyStr) {
    key = CryptoJS.enc.Utf8.parse(keyStr);
    iv = CryptoJS.enc.Utf8.parse(ivStr);
  }

  let base64 = CryptoJS.enc.Base64.parse(word);
  let src = CryptoJS.enc.Base64.stringify(base64);

  var decrypt = CryptoJS.AES.decrypt(src, key, {
    iv: iv,
    mode: CryptoJS.mode.CBC,
    padding: CryptoJS.pad.ZeroPadding
  });

  var decryptedStr = decrypt.toString(CryptoJS.enc.Utf8);
  return decryptedStr.toString();
}
```

在相关模块中引入
`import {Decrypt,Encrypt} from '@/plugins/cryptojs'`

在vue data()中定义 2个测试变量 d1,d2

在 template插入

```html
	<div>
     原数据： <el-input v-model="d1" placeholder="请输入内容"></el-input>
      <el-button type="primary" @click="jiami" plain>加密</el-button>
        <el-button type="primary" @click="jiemi" plain>解密</el-button>
        
     加密数据： <el-input v-model="d2" placeholder="请输入内容"></el-input>
 
    </div>
```



在methods 插入函数

```vue
jiami(){
      console.log("加密-----",this.d1);
      let dd = Encrypt(this.d1)
      console.log(dd)
      this.d2= dd
    },
    jiemi(){
      console.log("解密-----",this.d2);
      this.d1= Decrypt(this.d2)
    },
```

