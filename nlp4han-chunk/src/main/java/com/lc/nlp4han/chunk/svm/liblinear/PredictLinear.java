package com.lc.nlp4han.chunk.svm.liblinear;

import static com.lc.nlp4han.chunk.svm.liblinear.Linear.atof;
import static com.lc.nlp4han.chunk.svm.liblinear.Linear.atoi;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

public class PredictLinear extends Predict
{
	private static final Pattern COLON = Pattern.compile(":");
	
	/**
	 * <p>
	 * <b>Note: The streams are NOT closed</b>
	 * </p>
	 */
	public static double doPredict(String line, Model model)
	{
		int total = 0;

		int n;
		int nr_feature = model.getNrFeature();
		if (model.bias >= 0)
			n = nr_feature + 1;
		else
			n = nr_feature;

		
		
			List<Feature> x = new ArrayList<>();
			StringTokenizer st = new StringTokenizer(line, " \t\n");
			try
			{
				st.nextToken();
			}
			catch (NoSuchElementException e)
			{
				throw new RuntimeException("Wrong input format at line " + (total + 1), e);
			}

			while (st.hasMoreTokens())
			{
				String[] split = COLON.split(st.nextToken(), 2);
				if (split == null || split.length < 2)
				{
					throw new RuntimeException("Wrong input format at line " + (total + 1));
				}

				try
				{
					int idx = atoi(split[0]);
					double val = atof(split[1]);

					// feature indices larger than those in training are not used
					if (idx <= nr_feature)
					{
						Feature node = new FeatureNode(idx, val);
						x.add(node);
					}
				}
				catch (NumberFormatException e)
				{
					throw new RuntimeException("Wrong input format at line " + (total + 1), e);
				}
			}

			if (model.bias >= 0)
			{
				Feature node = new FeatureNode(n, model.bias);
				x.add(node);
			}

			Feature[] nodes = new Feature[x.size()];
			nodes = x.toArray(nodes);

			double predict_label;

			predict_label = Linear.predict(model, nodes);
			
			return predict_label;
			
		
	}
}
