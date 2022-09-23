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

    private static Domino referenceDomino = new Domino(2, 6);

    private static boolean validateDominoSides(Domino one, Domino two) {
        return (one.getSide1() == two.getSide1() && one.getSide2() == two.getSide2()) ||
                (one.getSide2() == two.getSide1() && one.getSide1() == two.getSide2());
    }

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void getDominoSetDoubleSix_Success() {
        List<Domino> dblSixSet = Domino.getDominoSet(Domino.SetType.DOUBLE_SIX);
        int numDominoes = dominoSets.get(Domino.SetType.DOUBLE_SIX).size();
        for (int idx = 0; idx < numDominoes; ++idx) {
            Domino expected = dominoSets.get(Domino.SetType.DOUBLE_SIX).get(idx);
            Domino actual = dblSixSet.get(idx);
            assertTrue(String.format("Expected Domino: %s matches actual Domino: %s", expected.toString(), actual.toString()), expected.equals(actual));
        }
    }

    @Test
    public void createDomino_Success() {
        Domino testDomino = new Domino(referenceDomino.getSide1(), referenceDomino.getSide2());
        assertTrue(String.format("Expected: Side values should be equivalent. Reference Domino: %s, created Domino: %s",
                referenceDomino.toString(), testDomino.toString()), validateDominoSides(testDomino, referenceDomino));
    }

    @Test
    public void isDouble_Success() {
    }

    @Test
    public void getOrientation_Success() {
    }

}