package org.nlp4han.sentiment.nb;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

/**
 * 保存一个已经分好类的文本和其所属类别的信息
 */
public class SentimentTextSample
{

	private final String category;
	private final String text;
	private final Map<String, Object> extraInformation;

	public SentimentTextSample(String category, String text)
	{
		this(category, text, null);
	}

	public SentimentTextSample(String category, String text, Map<String, Object> extraInformation)
	{
		Objects.requireNonNull(text, "text must not be null");

		this.category = Objects.requireNonNull(category, "category must not be null");
		this.text = text;

		if (extraInformation == null)
		{
			this.extraInformation = Collections.emptyMap();
		}
		else
		{
			this.extraInformation = extraInformation;
		}
	}

	public String getCategory()
	{
		return category;
	}

	public String getText()
	{
		return text;
	}

	public Map<String, Object> getExtraInformation()
	{
		return extraInformation;
	}

	@Override
	public String toString()
	{

		StringBuilder sampleString = new StringBuilder();

		sampleString.append(category).append('\t');
		sampleString.append(text);

		/*if (sampleString.length() > 0)
		{
			// remove last space
			sampleString.setLength(sampleString.length() - 1);
		}*/

		return sampleString.toString();
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(getCategory(), text.hashCode());
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
		{
			return true;
		}

		if (obj instanceof SentimentTextSample)
		{
			SentimentTextSample a = (SentimentTextSample) obj;

			return getCategory().equals(a.getCategory()) && text.equals(a.getText());
		}

		return false;
	}
}
