package geo.conekta.minth;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import java.awt.Desktop;
import java.io.File;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.wb.swt.SWTResourceManager;

public class MainForm  {

	protected Shell shlExtraerTorqueY;
	private Text txtExtraerDe;
	public static Display display = Display.getDefault();

	String selected = "D:\\DATOS CSV EXTRACTOR 2\\";
	
	public static Label myOutput;
	public static ProgressBar progressBar1;

    private ReadCsv myProcess = null;

	String myPath=null;
	private Text text1;
	
	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			MainForm window = new MainForm();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Open the window.
	 */
	public void open() {
		
		createContents();
		shlExtraerTorqueY.open();
		shlExtraerTorqueY.layout();
		while (!shlExtraerTorqueY.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		shlExtraerTorqueY = new Shell(display,SWT.TITLE | SWT.CLOSE | SWT.BORDER);
		shlExtraerTorqueY.setImage(SWTResourceManager.getImage(MainForm.class, "/geo/conekta/minth/csv_reader_mVe_icon.ico"));
		shlExtraerTorqueY.setSize(962, 462);
		shlExtraerTorqueY.setText("Extraer Presión, Compresión, Torque y \u00C1ngulo"); 
        
        Button btnSalir = new Button(shlExtraerTorqueY, SWT.NONE);
        btnSalir.setFont(SWTResourceManager.getFont("Segoe UI", 10, SWT.NORMAL));
        btnSalir.addSelectionListener(new SelectionAdapter() {
        	@Override
        	public void widgetSelected(SelectionEvent e) {
                if (myProcess != null) {
                	myProcess.cancel();
                }
        	}
        });
        btnSalir.setBounds(690, 391, 99, 21);
        btnSalir.setText("Cancelar");
        btnSalir.setVisible(false);
		
        progressBar1 = new ProgressBar(shlExtraerTorqueY, SWT.HORIZONTAL);
        progressBar1.setFont(SWTResourceManager.getFont("Segoe UI", 12, SWT.NORMAL));
        progressBar1.setMinimum(0);
        progressBar1.setBounds(795, 391, 151, 21);
        progressBar1.setVisible(false);

		txtExtraerDe = new Text(shlExtraerTorqueY, SWT.BORDER);
		txtExtraerDe.setFont(SWTResourceManager.getFont("Segoe UI", 10, SWT.NORMAL));
		txtExtraerDe.setEditable(false);
		txtExtraerDe.setEnabled(false);
		txtExtraerDe.setText("D:\\GEOPANDA\\archivos");
		txtExtraerDe.setBounds(87, 76, 686, 35);
		
		Label lblNewLabel = new Label(shlExtraerTorqueY, SWT.NONE);
		lblNewLabel.setFont(SWTResourceManager.getFont("Segoe UI", 10, SWT.NORMAL));
		lblNewLabel.setBounds(39, 84, 40, 30);
		lblNewLabel.setText("Ruta");
		
		ScrolledComposite scrolledComposite = new ScrolledComposite(shlExtraerTorqueY, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		scrolledComposite.setToolTipText("Current action");
		scrolledComposite.setBounds(87, 139, 849, 213);
		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setExpandVertical(true);
		
		myOutput = new Label( scrolledComposite, SWT.NONE );
		myOutput.setBackground( display.getSystemColor( SWT.COLOR_WHITE ) );
		scrolledComposite.setContent(myOutput);
		scrolledComposite.setMinSize(myOutput.computeSize(SWT.DEFAULT, SWT.DEFAULT));

		Button btnProcesar = new Button(shlExtraerTorqueY, SWT.NONE);
		//crea el directorio csv2extract si no existe
		try {
			File folder = new File("c:\\csv2extract\\");
			folder.mkdirs(); 
		} catch(SecurityException e){
			//no tiene acceso a la unidad C: le pide al usuario que cambie o seleccione una ruta
			btnProcesar.setEnabled(false);
			System.out.println(e);
        }			

		DirectoryDialog fd = new DirectoryDialog(shlExtraerTorqueY, SWT.OPEN);
		
		Label lblArchivoLeido = new Label(shlExtraerTorqueY, SWT.NONE);
		lblArchivoLeido.setLocation(39, 391);
		lblArchivoLeido.setSize(645, 21);
		lblArchivoLeido.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.NORMAL));
		lblArchivoLeido.setText("Abriendo");
		lblArchivoLeido.setVisible(false);
		Button btnCambiarRuta = new Button(shlExtraerTorqueY, SWT.NONE);
		btnCambiarRuta.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false,1,0));
		btnCambiarRuta.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				fd.setText("Seleccionar");
				fd.setFilterPath("C:\\csv2extract");
				selected = fd.open();
				if (selected!= null)
					txtExtraerDe.setText(selected);
				else
					txtExtraerDe.setText(txtExtraerDe.getText());
					System.out.println(selected);
				}
		});
		btnCambiarRuta.setBounds(794, 77, 35, 35);
		btnCambiarRuta.setText("...");
		
		btnProcesar.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				btnProcesar.setEnabled(false);
				btnCambiarRuta.setEnabled(false);
				progressBar1.setVisible(true);
				text1.setVisible(false);
				lblArchivoLeido.setVisible(true);	
				btnSalir.setVisible(true);
				myPath = txtExtraerDe.getText();				
					myProcess = new ReadCsv(display,progressBar1, myPath, myOutput,text1,btnSalir,lblArchivoLeido, btnProcesar, btnCambiarRuta);
					myProcess.start();
			}
		});
		btnProcesar.setBounds(847, 77, 86, 35);
		btnProcesar.setText("Procesar");
		
		Label lblLog = new Label(shlExtraerTorqueY, SWT.NONE);
		lblLog.setBounds(39, 145, 35, 30);
		lblLog.setText("Log");
		

		
		ToolBar toolBar = new ToolBar(shlExtraerTorqueY, SWT.FLAT | SWT.RIGHT);
		toolBar.setBackground(SWTResourceManager.getColor(SWT.COLOR_GRAY));
		toolBar.setBounds(0, 0, 956, 35);
		
		ToolItem tltmManual = new ToolItem(toolBar, SWT.NONE);
		tltmManual.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					Desktop.getDesktop().open(new File("C:\\Program Files (x86)\\Minth\\MANUAL-USUARIO-MINTH.pdf"));
				}catch(Exception i) {
			        MessageBox messageBox = new MessageBox(shlExtraerTorqueY, SWT.ICON_INFORMATION | SWT.OK );		        
			        messageBox.setText("Alerta");
			        messageBox.setMessage("Hay un problema con el Manual de usuario, favor de ponerse en contacto con su proveedor.");
			        messageBox.open();			
			        System.out.println(i.getStackTrace().toString());
				}
			}
		});
		tltmManual.setText("Manual");
		
		text1 = new Text(shlExtraerTorqueY, SWT.BORDER);
		text1.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.NORMAL));
		text1.setBounds(857, 360, 76, 21);
		text1.setVisible(false);
	}
}