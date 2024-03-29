package test.DominoModelTesting;

import com.bduhbsoft.BigSixDominoes.Domino;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class DominoTest {

    private static final Map<Domino.SetType, List<Domino>> dominoSets;

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

    private static final Domino referenceDomino = new Domino(2, 6);
    private static final Domino referenceDominoDouble = new Domino(6, 6);

    private static boolean validateDominoSidesRaw(Domino one, Domino two) {
        return (one.getSide1() == two.getSide1() && one.getSide2() == two.getSide2()) ||
                (one.getSide2() == two.getSide1() && one.getSide1() == two.getSide2());
    }

    private static boolean validateDominoSidesInternalObject(Domino one, Domino two) {
        return (one.getDomSide1().getValue() == two.getDomSide1().getValue() &&
                one.getDomSide2().getValue() == two.getDomSide2().getValue()) ||
                (one.getDomSide1().getValue() == two.getDomSide2().getValue() &&
                one.getDomSide2().getValue() == two.getDomSide1().getValue());
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
        assertEquals(String.format("Expected Domino count: %s matches actual Domino count: %s",
                numDominoes, dblSixSet.size()), numDominoes, dblSixSet.size());
        for (int idx = 0; idx < numDominoes; ++idx) {
            Domino expected = dominoSets.get(Domino.SetType.DOUBLE_SIX).get(idx);
            Domino actual = dblSixSet.get(idx);
            assertTrue(String.format("Expected Domino: %s matches actual Domino: %s", expected.toString(), actual.toString()), expected.equals(actual));
        }
    }

    @Test
    public void createDominoRaw_Success() {
        Domino testDomino = new Domino(referenceDomino.getSide1(), referenceDomino.getSide2());
        assertTrue(String.format("Comparing side values directly. Expected: Side values should be equivalent. Reference Domino: %s, created Domino: %s",
                referenceDomino, testDomino), validateDominoSidesRaw(testDomino, referenceDomino));
        assertTrue(String.format("Comparing with object equals. Expected Domino: %s matches actual Domino: %s",
                referenceDomino, testDomino), referenceDomino.equals(testDomino));
        assertTrue(String.format("Comparing with internal side object. Expected: Side values should be equivalent. Reference Domino: %s, created Domino: %s",
                referenceDomino, testDomino), validateDominoSidesInternalObject(testDomino, referenceDomino));
    }

    @Test
    public void createDominoObject_Success() {
        Domino testDomino = new Domino(referenceDomino);
        assertTrue(String.format("Comparing side values directly. Expected: Side values should be equivalent. Reference Domino: %s, created Domino: %s",
                referenceDomino, testDomino), validateDominoSidesRaw(testDomino, referenceDomino));
        assertTrue(String.format("Comparing with object equals. Expected Domino: %s matches actual Domino: %s",
                referenceDomino, testDomino), referenceDomino.equals(testDomino));
        assertTrue(String.format("Comparing with internal side object. Expected: Side values should be equivalent. Reference Domino: %s, created Domino: %s",
                referenceDomino, testDomino), validateDominoSidesInternalObject(testDomino, referenceDomino));
    }

    @Test
    public void isDouble_Success() {
        Domino testDomino = new Domino(referenceDominoDouble);
        assertTrue(String.format("Expected Domino: %s is a double", testDomino), testDomino.isDouble());
    }

    @Test
    public void isDouble_Failure() {
        Domino testDomino = new Domino(referenceDomino);
        assertFalse(String.format("Expected Domino: %s is not a double", testDomino), testDomino.isDouble());
    }

    @Test
    public void getOrientation_Success() {
        final Domino.Orientation orientation = Domino.Orientation.SIDE1_WEST;
        Domino testDomino = new Domino(referenceDomino);
        testDomino.setOrientation(orientation);
        assertSame(String.format("Expected Orientation: %s matches actual Orientation: %s",
                orientation, testDomino.getOrientation()), orientation, testDomino.getOrientation());
    }

}