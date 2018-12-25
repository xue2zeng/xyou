package com.xyou.edu.core.util.codec;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.xyou.edu.core.util.common.ExceptionUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.text.StringEscapeUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.apache.commons.lang3.StringUtils.*;

/**
 * 封装各种格式的编码解码工具类. 1.Commons-Codec的 hex/base64 编码 2.Commons-Lang的xml/html escape 3.JDK提供的URLEncoder
 *
 * @author xue.zeng
 * @date 2018-12-15
 */
@Slf4j
public class EncodeUtils {

  /** Hex编码. */
  public static String encodeHex(byte[] input) {
    return new String(Hex.encodeHex(input));
  }

  /** Hex解码. */
  public static byte[] decodeHex(String input) {
    try {
      return Hex.decodeHex(input.toCharArray());
    } catch (DecoderException e) {
      throw ExceptionUtils.unchecked(e);
    }
  }

  /** Base64编码. */
  public static String encodeBase64(byte[] input) {
    return new String(Base64.encodeBase64(input));
  }

  /** Base64编码. */
  public static String encodeBase64(String input) {
    return new String(Base64.encodeBase64(input.getBytes(Charsets.UTF_8)));
  }

  /** Base64解码. */
  public static byte[] decodeBase64(String input) {
    return Base64.decodeBase64(input.getBytes());
  }

  /** Base64解码. */
  public static String decodeBase64String(String input) {
    return new String(Base64.decodeBase64(input.getBytes()), Charsets.UTF_8);
  }

  /** Html 转码. */
  public static String encodeHtml(String html) {
    return StringEscapeUtils.escapeHtml4(html);
  }

  /** Html 解码. */
  public static String decodeHtml(String htmlEscaped) {
    return StringEscapeUtils.unescapeHtml4(htmlEscaped);
  }

  /** Xml 转码. */
  public static String encodeXml(String xml) {
    return StringEscapeUtils.escapeXml10(xml);
  }

  /** Xml 解码. */
  public static String decodeXml(String xmlEscaped) {
    return StringEscapeUtils.unescapeXml(xmlEscaped);
  }

  /** URL 编码, Encode默认为UTF-8. */
  public static String encodeUrl(String part) {
    return encodeUrl(part, Charsets.UTF_8);
  }

  /** URL 编码, Encode默认为UTF-8. */
  public static String encodeUrl(String part, Charset charset) {
    if (Objects.isNull(part)) {
      return part;
    }
    try {
      return URLEncoder.encode(part, charset.name());
    } catch (UnsupportedEncodingException e) {
      throw ExceptionUtils.unchecked(e);
    }
  }

  /** URL 解码, Encode默认为UTF-8. */
  public static String decodeUrl(String part) {
    return decodeUrl(part, Charsets.UTF_8);
  }

  /** URL 解码, Encode默认为UTF-8. */
  public static String decodeUrl(String part, Charset charset) {
    if (Objects.isNull(part)) {
      return part;
    }
    try {
      return URLDecoder.decode(part, charset.name());
    } catch (UnsupportedEncodingException e) {
      throw ExceptionUtils.unchecked(e);
    }
  }

  /** URL 解码（两次）, Encode默认为UTF-8. */
  public static String decodeUrl2(String part) {
    return decodeUrl(decodeUrl(part));
  }

  /** 预编译XSS过滤正则表达式 */
  private static List<Pattern> xssPatterns =
      Lists.newArrayList(
          Pattern.compile(
              "(<\\s*(script|link|style|iframe)([\\s\\S]*?)(>|<\\/\\s*\\1\\s*>))|(</\\s*(script|link|style|iframe)\\s*>)",
              Pattern.CASE_INSENSITIVE),
          Pattern.compile(
              "\\s*(href|src)\\s*=\\s*(\"\\s*(javascript|vbscript):[^\"]+\"|'\\s*(javascript|vbscript):[^']+'|(javascript|vbscript):[^\\s]+)\\s*(?=>)",
              Pattern.CASE_INSENSITIVE),
          Pattern.compile(
              "\\s*on[a-z]+\\s*=\\s*(\"[^\"]+\"|'[^']+'|[^\\s]+)\\s*(?=>)",
              Pattern.CASE_INSENSITIVE),
          Pattern.compile(
              "(eval\\((.|\\n)*\\)|xpression\\((.|\\n)*\\))", Pattern.CASE_INSENSITIVE));

  /**
   * XSS 非法字符过滤，内容以
   * <!--HTML-->
   * 开头的用以下规则（保留标签）
   */
  public static String xssFilter(String text) {
    String oriValue = trim(text);
    if (text != null) {
      String value = oriValue;
      for (Pattern pattern : xssPatterns) {
        Matcher matcher = pattern.matcher(value);
        if (matcher.find()) {
          value = matcher.replaceAll(EMPTY);
        }
      }

      value = getEscapeString(value);
      if (log.isInfoEnabled() && !value.equals(oriValue)) {
        log.info("xssFilter: {}   <=<=<=   {}", value, text);
      }
      return value;
    }
    return null;
  }

  /**
   * 如果不是HTML，XML，JFlow，JSON格式，再对HTML的 "、<、> 转码
   *
   * @param value 转义前的数据
   * @return 转义后的数据
   */
  private static String getEscapeString(String value) {
    // HTML、XML、JFlow、JSON Object、JSON Array
    boolean isGuessText =
        !startsWithIgnoreCase(value, "<!--HTML-->")
            && !startsWithIgnoreCase(value, "<?xml ")
            && !contains(value, "id=\"FormHtml\"")
            && !(startsWith(value, "{") && endsWith(value, "}"))
            && !(startsWith(value, "[") && endsWith(value, "]"));
    if (isGuessText) {
      StringBuilder sb = new StringBuilder();
      for (int i = 0; i < value.length(); i++) {
        char c = value.charAt(i);
        switch (c) {
          case '>':
            sb.append("＞");
            break;
          case '<':
            sb.append("＜");
            break;
          case '\'':
            sb.append("＇");
            break;
          case '\"':
            sb.append("＂");
            break;
          default:
            sb.append(c);
            break;
        }
      }
      value = sb.toString();
    }
    return value;
  }

  /**
   * 预编译SQL过滤正则表达式
    */
  private static Pattern sqlPattern =
      Pattern.compile(
              "(?:')|(?:--)|(/\\*(?:.|[\\n" +
                      "\\r])*?\\*/)|(\\b(select|update|and|or|delete|insert|trancate|char|substr|ascii|declare" +
                      "|exec|count|master|into|drop|execute)\\b)",
          Pattern.CASE_INSENSITIVE);

  /** SQL过滤，防止注入，传入参数输入有select相关代码，替换空 */
  public static String sqlFilter(String text) {
    if (text != null) {
      String value = text;
      Matcher matcher = sqlPattern.matcher(text);
      if (matcher.find()) {
        value = matcher.replaceAll(EMPTY);
      }
      if (log.isDebugEnabled()) {
        log.debug("sqlFilter: [{}]   ===>   [{}]", text, value);
        return value;
      }
      return value;
    }
    return text;
  }
}
