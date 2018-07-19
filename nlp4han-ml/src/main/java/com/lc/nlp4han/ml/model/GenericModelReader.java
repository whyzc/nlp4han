package com.lc.nlp4han.ml.model;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import com.lc.nlp4han.ml.maxent.gis.GISModelReader;
import com.lc.nlp4han.ml.maxent.quasinewton.QNModelReader;
import com.lc.nlp4han.ml.naivebayes.NaiveBayesModelReader;
import com.lc.nlp4han.ml.perceptron.PerceptronModelReader;

public class GenericModelReader extends AbstractModelReader {

  private AbstractModelReader delegateModelReader;

  public GenericModelReader (File f) throws IOException {
    super(f);
  }
  
  public GenericModelReader(InputStream input, boolean binary) throws IOException {
      super(input, binary);
    }
  
  
  public GenericModelReader(InputStream input) throws IOException {
      super(input);
    }

  public GenericModelReader(DataReader dataReader) {
    super(dataReader);
  }

  public void checkModelType() throws IOException {
    String modelType = readUTF();
    switch (modelType) {
      case "Perceptron":
        delegateModelReader = new PerceptronModelReader(this.dataReader);
        break;
      case "GIS":
        delegateModelReader = new GISModelReader(this.dataReader);
        break;
      case "QN":
        delegateModelReader = new QNModelReader(this.dataReader);
        break;
      case "NaiveBayes":
        delegateModelReader = new NaiveBayesModelReader(this.dataReader);
        break;
      default:
        throw new IOException("Unknown model format: " + modelType);
    }
  }


  public AbstractModel constructModel() throws IOException {
    return delegateModelReader.constructModel();
  }

  public static void main(String[] args) throws IOException {
    AbstractModel m =  new GenericModelReader(new File(args[0])).getModel();
    new GenericModelWriter( m, new File(args[1])).persist();
  }
}