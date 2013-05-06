package org.ncibi.cytoscape.mimi;

public class MiMIURL {
	private static final String MIMIPLUGIN_WEB = "http://mimiplugin.ncibi.org";
	private static final String DBACCESS = MIMIPLUGIN_WEB + "/dbaccess/";
	private static final String VERSION = "3.2";
	
	private static final String MIMI_WEB = "http://mimitest.ncibi.org/mimi";
	
	public static final String LOGO="http://www.bioinformatics.med.umich.edu/app/nlp/logo/";	
	public static final String BIONLPURL="https://portal.ncibi.org/portal/site/!gateway/page/9b2c0673-4252-409d-003d-25996426b215";
	public static final String GENE2MESH="http://gene2mesh.ncibi.org/";
	public static final String MESHSEARCH="http://gene2mesh.ncibi.org/viewlist.php";//?term=prostatic final+neoplasms&qtype=mesh";

	public static final String MIMINODELINK=MIMI_WEB + "/gene"; //?geneid=1436
	public static final String FREETEXTSEARCH=MIMI_WEB + "/symbols";//?query=Prostate+Cancer

	public static final String MIMIAPPHOME=MIMIPLUGIN_WEB;
	public static final String GENESAMPLE=MIMIPLUGIN_WEB +"/mygene.txt";
	public static final String GENEFILETEMPLATE=MIMIPLUGIN_WEB + "/template.html";
	public static final String CHECKAPPVERSION=MIMIPLUGIN_WEB + "/CurrentPluginVersion";
	
	public static final String ANNOTEDITORLOGIN=DBACCESS + VERSION + "/queryLogin.php";
	public static final String NEWUSERURL=DBACCESS + VERSION + "/addNewUser.php";
	public static final String VALIDATEEMAIL=DBACCESS + VERSION + "/validateEmail.php";
	public static final String GETSHAREDANNOT=DBACCESS + VERSION + "/getSharedAnnot.php";
	public static final String ANNOTSETNAME=DBACCESS + VERSION + "/queryMiMIAnnot_setList.php";
	public static final String SENDPSWD=DBACCESS + VERSION + "/sendpswd.php";
	public static final String PRECOMPUTEEXPAND=DBACCESS + VERSION + "/precomputeexpand.php";
	public static final String QUERYGDS=DBACCESS + VERSION + "/queryGDS_subsetExprSig.php";
	public static final String BROWSEGDS=DBACCESS + VERSION + "/browseGds.php";
	public static final String GETINTERACTION=DBACCESS + VERSION + "/getInteraction.php";
	public static final String GETATTRIBUTES=DBACCESS + VERSION + "/getAttributes.php";
	public static final String CHECKUSERANNOATTR=DBACCESS + VERSION + "/checkUserAnnotAttribute.php";
}
