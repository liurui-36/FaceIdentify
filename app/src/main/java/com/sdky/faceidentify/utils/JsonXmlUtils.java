package com.sdky.faceidentify.utils;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import java.io.IOException;
import java.io.StringWriter;
import java.util.List;

/**
 *
 */
public class JsonXmlUtils {
	
	/**
	 * 将对象转换成xml格式输出
	 * 
	 * */
	public static String writeObjectToXml(Object object) {
		StringWriter sw = new StringWriter();
		XmlMapper xml = new XmlMapper();
		try {
			xml.writeValue(sw, object);

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		return sw.toString();
	}

	/**
	 * 将对象列表转换成xml格式输出
	 * 
	 * */
	public static String writeObjectToXml(List<Object> list) {
		StringWriter sw = new StringWriter();
		XmlMapper xml = new XmlMapper();
		try {
			xml.writeValue(sw, list);

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		return sw.toString();
	}

	/**
	 * 将对象转换成json数据
	 * */
	public static String writeObjectToJson(Object object) {
		ObjectMapper mapper = new ObjectMapper();
		String str = null;
		try {
			str = mapper.writeValueAsString(object);
		} catch (JsonProcessingException e) {

		}
		return str;
	}

	/**
	 * 将对象列表转换成json数据
	 * */
	public static String writeObjectToJson(List<Object> list) {
		ObjectMapper mapper = new ObjectMapper();
		String str = null;
		try {
			str = mapper.writeValueAsString(list);
		} catch (JsonProcessingException e) {

		}
		return str;
	}

	/**
	 * 将xml写入到对象,源码在这方面有BUG，用字符串处理可以解决问题
	 * */
	public static <T> T writeXmlToObject(Class<T> valueType, String xmlString) {
		XmlMapper xml = new XmlMapper();
		T t = null;
		try {
			t = xml.readValue(xmlString, valueType);

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		return t;
	}

	/**
	 * 将Json写入到对象
	 * 
	 * @param <T>
	 * 
	 * @return
	 * @throws IOException
	 * @throws JsonMappingException
	 * @throws JsonParseException
	 * */
	public static <T> T writeJsonToObject(Class<T> valueType, String json) throws JsonParseException, JsonMappingException, IOException {
		
		ObjectMapper mapper = new ObjectMapper();
		T t = null;
		t = mapper.readValue(json.getBytes(), valueType);
		return t;
	}
	
	
}
