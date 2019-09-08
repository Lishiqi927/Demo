package com.java.controller;

import com.alibaba.fastjson.JSONObject;
import com.java.pojo.AipSpeech;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.InputStream;

@Controller
@RequestMapping("/test")
public class Test {
    // 设置APPID/AK/SK，注册百度语音识别API即可获取
    public static final String APP_ID = "1257358139";
    public static final String API_KEY = "AKID0lKPM381NdkjUDS3BwhOs7IFwQTPcarz";
    public static final String SECRET_KEY = "GHFaonaBdSGxXvNTM9cvhKiAScgcmw52";

    @RequestMapping("/con")
    public Object speechReco(HttpServletRequest request) {
        System.out.println("+++++++++++++++++++++");
        MultipartFile file = ((MultipartHttpServletRequest) request).getFile("file");
        try {
            byte[] pcmBytes = mp3Convertpcm(file.getInputStream());
            JSONObject resultJson = speechBdApi(pcmBytes);
            System.out.println(resultJson.toString());
            if (null != resultJson && resultJson.getInteger("err_no") == 0) {
                return resultJson.getJSONArray("result").get(0).toString().split("，")[0];
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    /**
     * @param mp3Stream 原始文件流
     * @return 转换后的二进制
     * @throws Exception
     * @Description MP3转换pcm
     * @author liuyang
     * @blog http://www.pqsky.me
     * @date 2018年1月30日
     */
    public byte[] mp3Convertpcm(InputStream mp3Stream) throws Exception {
        // 原MP3文件转AudioInputStream
        AudioInputStream mp3audioStream = AudioSystem.getAudioInputStream(mp3Stream);
        // 将AudioInputStream MP3文件 转换为PCM AudioInputStream
        AudioInputStream pcmaudioStream = AudioSystem.getAudioInputStream(AudioFormat.Encoding.PCM_SIGNED,
                mp3audioStream);
        byte[] pcmBytes = IOUtils.toByteArray(pcmaudioStream);
        pcmaudioStream.close();
        mp3audioStream.close();
        return pcmBytes;
    }

    /**
     * @param pcmBytes
     * @return
     * @Description 调用百度语音识别API
     * @author liuyang
     * @blog http://www.pqsky.me
     * @date 2018年1月30日
     */
    public static JSONObject speechBdApi(byte[] pcmBytes) {
        // 初始化一个AipSpeech
        AipSpeech client = new AipSpeech(APP_ID, API_KEY, SECRET_KEY);
        // 可选：设置网络连接参数
        client.setConnectionTimeoutInMillis(2000);
        client.setSocketTimeoutInMillis(60000);
        // 调用接口
        JSONObject res = client.asr(pcmBytes, "pcm", 16000, null);
        return res;
    }
}
