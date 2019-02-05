package rcomic.utils;

/**
 * 管理目前是否輸出Log<br>
 * <br>
 * <br>
 * 單一物件印Log使用方式:<br>
 * public class <B>Abc</B> {<br>
 * private static Log mLog = Log.newInstance(<B>true</B>);<br>
 * }<br>
 * <br>
 * 用動態方式取得目前class名稱印Log，使用方式:<br>
 * Log.logDGlobal("hello bug");<br>
 * 
 * @author Ray
 * @version 2014.04.09
 * 
 */
public class Log {
	private final static String CLASS_NAME = Log.class.getName();
	private final static String PRINT_GLOABLE = CLASS_NAME + "printGlobal";
	private final static String PRINTLN_GLOABLE = CLASS_NAME + "printlnGlobal";
	/** 此變數若為是所有log都會影嚮 */
	private static boolean sGlobalDebug = true;
	/** 只影嚮目前Log物件 */
	private boolean mDebug = true;
	private String mClassName = "";

	private Log(String simpleName) {
		mClassName = simpleName;
	}

	/**
	 * @param debug
	 *            true:目前印出log，false:不印log
	 * @return
	 */
	public static Log newInstance(boolean debug) {
		Log o = new Log(getClassName(CLASS_NAME + "newInstance"));
		o.setDebug(debug);
		return o;
	}

	/**
	 * 開關所有的Log物件是否輸出log
	 * 
	 * @param b
	 */
	public static void setGlobalDebug(boolean b) {
		sGlobalDebug = b;
	}

	/**
	 * 取得門前輸出log的class name
	 * 
	 * @return
	 */
	public String getClassName() {
		return mClassName;
	}

	/**
	 * 開關目前Log物件是否輸出log
	 * 
	 * @param b
	 */
	public void setDebug(boolean b) {
		mDebug = b;
	}

	/**
	 * 輸出System.out.print(obj) note:當global_debug為false 或 debug為false時，將不會輸出log
	 * 
	 * @param obj
	 */
	public void print(Object obj) {
		if (sGlobalDebug && mDebug) {
			System.out.print(mClassName + " : " + obj);
		}
	}

	/**
	 * 輸出System.out.println(obj) note:當global_debug為false 或 debug為false時，將不會輸出log
	 * 
	 * @param obj
	 */
	public void println(Object obj) {
		if (sGlobalDebug && mDebug) {
			System.out.println(mClassName + " : " + obj);
		}
	}

	/**
	 * 用動態方式取得class name使用System.out.print(obj)<br>
	 * note:當global_debug為false時，將不會輸出log
	 * 
	 * @param obj
	 */
	public static void printGlobal(Object obj) {
		if (sGlobalDebug) {
			System.out.print(getClassName(PRINT_GLOABLE) + " : " + obj);
		}
	}

	/**
	 * 用動態方式取得class name使用System.out.print(obj)<br>
	 * note:當global_debug為false時，將不會輸出log
	 * 
	 * @param obj
	 */
	public static void printlnGlobal(Object obj) {
		if (sGlobalDebug) {
			System.out.println(getClassName(PRINTLN_GLOABLE) + " : " + obj);
		}
	}

	/**
	 * 取得目前呼叫此元件的class name
	 * 
	 * @param name
	 * @return
	 */
	private static String getClassName(String name) {
		String className = null;
		StackTraceElement[] stack = Thread.currentThread().getStackTrace();

		for (int i = 0; i < stack.length; i++) {
			StackTraceElement st = stack[i];
			String lineName = st.getClassName() + st.getMethodName();

			if (name.equals(lineName) && (i + 1) < stack.length) {
				className = stack[i + 1].getClassName();

				int pLastIndex = className.lastIndexOf('.');

				if (pLastIndex != -1) {
					className = className.substring(pLastIndex + 1);
				}
				break;
			}
		}
		return "" + className;
	}
}
