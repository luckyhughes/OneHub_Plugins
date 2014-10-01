package kg.apc.jmeter.functions;

import junit.framework.TestCase;
import kg.apc.emulators.TestJMeterUtils;
import org.apache.jmeter.engine.util.CompoundVariable;
import org.apache.jmeter.threads.JMeterContext;
import org.apache.jmeter.threads.JMeterContextService;
import org.apache.jmeter.threads.JMeterVariables;
import org.junit.BeforeClass;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Base64EncodeTest extends TestCase {
    @BeforeClass
    public static void setUpClass() throws Exception {
        TestJMeterUtils.createJmeterEnv();
        JMeterContextService.getContext().setVariables(new JMeterVariables());
    }

    public void testExecute() throws Exception {
        JMeterContext context = JMeterContextService.getContext();
        context.setVariables(new JMeterVariables());

        Collection<CompoundVariable> parameters = new ArrayList<CompoundVariable>();
        parameters.add(new CompoundVariable("test string"));
        parameters.add(new CompoundVariable("b64enc_res"));
        Base64Encode instance = new Base64Encode();
        instance.setParameters(parameters);

        String res = instance.execute(null, null);
        assertEquals("dGVzdCBzdHJpbmc=", res);
        assertNotNull(context.getVariables().get("b64enc_res"));
    }

    public void testGetReferenceKey() throws Exception {
        System.out.println("getReferenceKey");
        Base64Encode instance = new Base64Encode();
        String expResult = "__base64Encode";
        String result = instance.getReferenceKey();
        assertEquals(expResult, result);
    }

    public void testGetArgumentDesc() throws Exception {
        System.out.println("getArgumentDesc");
        Base64Encode instance = new Base64Encode();
        List result = instance.getArgumentDesc();
        assertEquals(2, result.size());
    }
}