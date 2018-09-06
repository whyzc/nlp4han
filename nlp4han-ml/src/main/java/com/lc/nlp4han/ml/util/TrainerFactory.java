package com.lc.nlp4han.ml.util;

import java.lang.reflect.Constructor;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.lc.nlp4han.ml.maxent.gis.GIS;
import com.lc.nlp4han.ml.maxent.quasinewton.QNTrainer;
import com.lc.nlp4han.ml.naivebayes.NaiveBayesTrainer;
import com.lc.nlp4han.ml.perceptron.PerceptronTrainer;
import com.lc.nlp4han.ml.perceptron.SimplePerceptronSequenceTrainer;

public class TrainerFactory {

  public enum TrainerType {
    EVENT_MODEL_TRAINER,
    EVENT_MODEL_SEQUENCE_TRAINER,
    SEQUENCE_TRAINER
  }

  // built-in trainers
  private static final Map<String, Class> BUILTIN_TRAINERS;

  static {
    Map<String, Class> _trainers = new HashMap<>();
    _trainers.put(GIS.MAXENT_VALUE, GIS.class);
    _trainers.put(QNTrainer.MAXENT_QN_VALUE, QNTrainer.class);
    _trainers.put(PerceptronTrainer.PERCEPTRON_VALUE, PerceptronTrainer.class);
    _trainers.put(SimplePerceptronSequenceTrainer.PERCEPTRON_SEQUENCE_VALUE,
        SimplePerceptronSequenceTrainer.class);
    _trainers.put(NaiveBayesTrainer.NAIVE_BAYES_VALUE, NaiveBayesTrainer.class);

    BUILTIN_TRAINERS = Collections.unmodifiableMap(_trainers);
  }

  /**
   * Determines the trainer type based on the ALGORITHM_PARAM value.
   *
   * @param trainParams
   * @return the trainer type or null if type couldn't be determined.
   */
  public static TrainerType getTrainerType(Map<String, String> trainParams){

    String alogrithmValue = trainParams.get(AbstractTrainer.ALGORITHM_PARAM);

    // Check if it is defaulting to the MAXENT trainer
    if (alogrithmValue == null) {
      return TrainerType.EVENT_MODEL_TRAINER;
    }

    Class<?> trainerClass = BUILTIN_TRAINERS.get(alogrithmValue);

    if(trainerClass != null) {

      if (EventTrainer.class.isAssignableFrom(trainerClass)) {
        return TrainerType.EVENT_MODEL_TRAINER;
      }
      else if (EventModelSequenceTrainer.class.isAssignableFrom(trainerClass)) {
        return TrainerType.EVENT_MODEL_SEQUENCE_TRAINER;
      }
      else if (SequenceTrainer.class.isAssignableFrom(trainerClass)) {
        return TrainerType.SEQUENCE_TRAINER;
      }
    }

    return null;
  }

  public static SequenceTrainer getSequenceModelTrainer(Map<String, String> trainParams,
      Map<String, String> reportMap) {
    String trainerType = trainParams.get(AbstractTrainer.ALGORITHM_PARAM);

    if (trainerType != null) {
      if (BUILTIN_TRAINERS.containsKey(trainerType)) {
        SequenceTrainer trainer =  TrainerFactory.<SequenceTrainer> createBuiltinTrainer(
            BUILTIN_TRAINERS.get(trainerType));
        trainer.init(trainParams, reportMap);
        return trainer;
      } 
    }
    
    return null;
  }

  public static EventModelSequenceTrainer getEventModelSequenceTrainer(Map<String, String> trainParams,
      Map<String, String> reportMap) {
    String trainerType = trainParams.get(AbstractTrainer.ALGORITHM_PARAM);
    if (trainerType != null) {
      if (BUILTIN_TRAINERS.containsKey(trainerType)) {
        EventModelSequenceTrainer trainer = TrainerFactory.<EventModelSequenceTrainer> createBuiltinTrainer(
            BUILTIN_TRAINERS.get(trainerType));
        trainer.init(trainParams, reportMap);
        return trainer;
      } 
    }
    
    return null;
  }

  public static EventTrainer getEventTrainer(Map<String, String> trainParams,
      Map<String, String> reportMap) {
    String trainerType = trainParams.get(AbstractTrainer.ALGORITHM_PARAM);
    if (trainerType == null) {
      // default to MAXENT
      AbstractEventTrainer trainer = new GIS();
      trainer.init(trainParams, reportMap);
      return trainer;
    }
    else {
      if (BUILTIN_TRAINERS.containsKey(trainerType)) {
        EventTrainer trainer = TrainerFactory.<EventTrainer> createBuiltinTrainer(
            BUILTIN_TRAINERS.get(trainerType));
        trainer.init(trainParams, reportMap);
        return trainer;
      } else {
        return null;
      }
    }
  }

  public static boolean isValid(Map<String, String> trainParams) {

    // TODO: Need to validate all parameters correctly ... error prone?!

    String algorithmName = trainParams.get(AbstractTrainer.ALGORITHM_PARAM);

    // If a trainer type can be determined, then the trainer is valid!
    if (algorithmName != null &&
        !(BUILTIN_TRAINERS.containsKey(algorithmName) || getTrainerType(trainParams) != null)) {
      return false;
    }

    try {
      String cutoffString = trainParams.get(AbstractTrainer.CUTOFF_PARAM);
      if (cutoffString != null) Integer.parseInt(cutoffString);

      String iterationsString = trainParams.get(AbstractTrainer.ITERATIONS_PARAM);
      if (iterationsString != null) Integer.parseInt(iterationsString);
    }
    catch (NumberFormatException e) {
      return false;
    }

    String dataIndexer = trainParams.get(AbstractEventTrainer.DATA_INDEXER_PARAM);

    if (dataIndexer != null) {
      if (!(AbstractEventTrainer.DATA_INDEXER_ONE_PASS_VALUE.equals(dataIndexer)
          || AbstractEventTrainer.DATA_INDEXER_TWO_PASS_VALUE.equals(dataIndexer))) {
        return false;
      }
    }

    // TODO: Check data indexing ...

    return true;
  }

  private static <T> T createBuiltinTrainer(Class<T> trainerClass) {
    T theTrainer = null;
    if (trainerClass != null) {
      try {
        Constructor<T> contructor = trainerClass.getConstructor();
        theTrainer = contructor.newInstance();
      } catch (Exception e) {
        String msg = "Could not instantiate the "
            + trainerClass.getCanonicalName()
            + ". The initialization throw an exception.";
        System.err.println(msg);
        e.printStackTrace();
        throw new IllegalArgumentException(msg, e);
      }
    }

    return theTrainer;
  }
}
