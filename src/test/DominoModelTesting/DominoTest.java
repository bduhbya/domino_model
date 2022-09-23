package test.DominoModelTesting;

import com.bduhbsoft.BigSixDominoes.Domino;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import sun.plugin.dom.DOMObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class DominoTest {

    private static Map<Domino.SetType, List<Domino>> dominoSets = null;

    static {
        dominoSets = new HashMap<>();
        dominoSets.put(Domino.SetType.DOUBLE_SIX, new ArrayList<Domino>(){
        {
           add(new Domino(0, 0));
           add(new Domino(0, 1));
           add(new Domino(0, 2));
           add(new Domino(0, 3));
           add(new Domino(0, 4));
           add(new Domino(0, 5));
           add(new Domino(0, 6));
           add(new Domino(1, 1));
           add(new Domino(1, 2));
           add(new Domino(1, 3));
           add(new Domino(1, 4));
           add(new Domino(1, 5));
           add(new Domino(1, 6));
           add(new Domino(2, 2));
           add(new Domino(2, 3));
           add(new Domino(2, 4));
           add(new Domino(2, 5));
           add(new Domino(2, 6));
           add(new Domino(3, 3));
           add(new Domino(3, 4));
           add(new Domino(3, 5));
           add(new Domino(3, 6));
           add(new Domino(4, 4));
           add(new Domino(4, 5));
           add(new Domino(4, 6));
           add(new Domino(5, 5));
           add(new Domino(5, 6));
           add(new Domino(6, 6));
        }}
        );
    }

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void getDominoSetDoubleSix() {
        List<Domino> dblSixSet = Domino.getDominoSet(Domino.SetType.DOUBLE_SIX);
        int numDominoes = dominoSets.get(Domino.SetType.DOUBLE_SIX).size();
        for (int idx = 0; idx < numDominoes; ++idx) {
            Domino expected = dominoSets.get(Domino.SetType.DOUBLE_SIX).get(idx);
            Domino actual = dblSixSet.get(idx);
            assertTrue(String.format("Expected Domino: %s matches actial Domino: %s", expected.toString(), actual.toString()), expected.equals(actual));
        }
    }

    @Test
    public void getSide1() {
    }

    @Test
    public void getSide2() {
    }

    @Test
    public void getDomSide1() {
    }

    @Test
    public void getDomSide2() {
    }

    @Test
    public void isDouble() {
    }

    @Test
    public void getOrientation() {
    }

    @Test
    public void setOrientation() {
    }
}