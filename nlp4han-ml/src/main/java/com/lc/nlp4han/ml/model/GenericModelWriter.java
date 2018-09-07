package com.lc.nlp4han.ml.model;

import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.zip.GZIPOutputStream;

import com.lc.nlp4han.ml.maxent.gis.BinaryGISModelWriter;
import com.lc.nlp4han.ml.maxent.gis.PlainTextGISModelWriter;
import com.lc.nlp4han.ml.maxent.quasinewton.BinaryQNModelWriter;
import com.lc.nlp4han.ml.model.AbstractModel.ModelType;
import com.lc.nlp4han.ml.naivebayes.BinaryNaiveBayesModelWriter;
import com.lc.nlp4han.ml.perceptron.BinaryPerceptronModelWriter;
import com.lc.nlp4han.ml.perceptron.PlainTextPerceptronModelWriter;


public class GenericModelWriter extends AbstractModelWriter {

  private AbstractModelWriter delegateWriter;

  public GenericModelWriter(AbstractModel model, File file) throws IOException {
    String filename = file.getName();
    OutputStream os;
    // handle the zipped/not zipped distinction
    if (filename.endsWith(".gz")) {
      os = new GZIPOutputStream(new FileOutputStream(file));
      filename = filename.substring(0, filename.length() - 3);
    } else {
      os = new FileOutputStream(file);
    }

    // handle the different formats
    if (filename.endsWith(".bin")) {
      init(model, new DataOutputStream(os));
    } else {  // filename ends with ".txt"
      init(model, new BufferedWriter(new OutputStreamWriter(os)));
    }
  }

  public GenericModelWriter(AbstractModel model, DataOutputStream dos) {
    init(model, dos);
  }

  private void init(AbstractModel model, DataOutputStream dos) {
    if (model.getModelType() == ModelType.Perceptron) {
      delegateWriter = new BinaryPerceptronModelWriter(model, dos);
    } else if (model.getModelType() == ModelType.Maxent) {
      delegateWriter = new BinaryGISModelWriter(model, dos);
    } else if (model.getModelType() == ModelType.MaxentQn) {
      delegateWriter = new BinaryQNModelWriter(model, dos);
    }
    else if (model.getModelType() == ModelType.NaiveBayes) {
      delegateWriter = new BinaryNaiveBayesModelWriter(model, dos);
    }
  }

  private void init(AbstractModel model, BufferedWriter bw) {
    if (model.getModelType() == ModelType.Perceptron) {
      delegateWriter = new PlainTextPerceptronModelWriter(model, bw);
    } else if (model.getModelType() == ModelType.Maxent) {
      delegateWriter = new PlainTextGISModelWriter(model, bw);
    }
//    if (model.getModelType() == ModelType.NaiveBayes) {
//      delegateWriter = new PlainTextNaiveBayesModelWriter(model, bw);
//    }
  }

  @Override
  public void close() throws IOException {
    delegateWriter.close();
  }

  @Override
  public void persist() throws IOException {
    delegateWriter.persist();
  }

  @Override
  public void writeDouble(double d) throws IOException {
    delegateWriter.writeDouble(d);
  }

  @Override
  public void writeInt(int i) throws IOException {
    delegateWriter.writeInt(i);
  }

  @Override
  public void writeUTF(String s) throws IOException {
    delegateWriter.writeUTF(s);
  }
}