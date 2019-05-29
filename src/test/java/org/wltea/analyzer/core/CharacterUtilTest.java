package org.wltea.analyzer.core;

import org.junit.Assert;
import org.junit.Test;

public class CharacterUtilTest {

  @Test
  public void testIdentifyCharType0() {
    Assert.assertEquals(0, CharacterUtil.identifyCharType('!'));
  }

  @Test
  public void testIdentifyCharType1() {
    Assert.assertEquals(1, CharacterUtil.identifyCharType('1'));
  }

  @Test
  public void testIdentifyCharType2() {
    Assert.assertEquals(2, CharacterUtil.identifyCharType('q'));
    Assert.assertEquals(2, CharacterUtil.identifyCharType('Q'));
  }

  @Test
  public void testIdentifyCharType4() {
    Assert.assertEquals(4, CharacterUtil.identifyCharType('\u4E00'));
    Assert.assertEquals(4, CharacterUtil.identifyCharType('\uF900'));
    Assert.assertEquals(4, CharacterUtil.identifyCharType('\u3400'));
  }

  @Test
  public void testIdentifyCharType8() {
    Assert.assertEquals(8, CharacterUtil.identifyCharType('\uFF01'));
    Assert.assertEquals(8, CharacterUtil.identifyCharType('\uAC00'));
    Assert.assertEquals(8, CharacterUtil.identifyCharType('\u1100'));
    Assert.assertEquals(8, CharacterUtil.identifyCharType('\u3131'));
    Assert.assertEquals(8, CharacterUtil.identifyCharType('\u3041'));
    Assert.assertEquals(8, CharacterUtil.identifyCharType('\u30A0'));
    Assert.assertEquals(8, CharacterUtil.identifyCharType('\u31F0'));
  }

  @Test
  public void testRegularize() {
    Assert.assertEquals(' ', CharacterUtil.regularize('　'));
    Assert.assertEquals('!', CharacterUtil.regularize('！'));
    Assert.assertEquals('a', CharacterUtil.regularize('A'));
  }
}
