package com.aptana.deploy.wizard;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Text;

import com.aptana.deploy.Activator;
import com.aptana.deploy.HerokuAPI;

public class HerokuLoginWizardPage extends WizardPage
{
	private static final String NAME = "HerokuLogin"; //$NON-NLS-1$
	private static final String HEROKU_ICON = "icons/heroku.png"; //$NON-NLS-1$

	private Text userId;
	private Text password;

	protected HerokuLoginWizardPage()
	{
		super(NAME, Messages.HerokuLoginWizardPage_Title, Activator.getImageDescriptor(HEROKU_ICON));
	}

	@Override
	public void createControl(Composite parent)
	{
		Composite composite = new Composite(parent, SWT.NULL);
		composite.setLayout(new GridLayout());
		composite.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_FILL | GridData.HORIZONTAL_ALIGN_FILL));
		setControl(composite);

		initializeDialogUnits(parent);

		// Actual contents
		Label label = new Label(composite, SWT.NONE);
		label.setText(Messages.HerokuLoginWizardPage_EnterCredentialsLabel);

		Composite credentials = new Composite(composite, SWT.NONE);
		credentials.setLayout(new GridLayout(2, false));

		Label userIdLabel = new Label(credentials, SWT.NONE);
		userIdLabel.setText(Messages.HerokuLoginWizardPage_UserIDLabel);
		userId = new Text(credentials, SWT.SINGLE | SWT.BORDER);
		userId.setMessage(Messages.HerokuLoginWizardPage_UserIDExample);
		GridData gd = new GridData(300, SWT.DEFAULT);
		userId.setLayoutData(gd);
		userId.addModifyListener(new ModifyListener()
		{

			@Override
			public void modifyText(ModifyEvent e)
			{
				getContainer().updateButtons();
			}
		});

		Label passwordLabel = new Label(credentials, SWT.NONE);
		passwordLabel.setText(Messages.HerokuLoginWizardPage_PasswordLabel);
		password = new Text(credentials, SWT.SINGLE | SWT.BORDER | SWT.PASSWORD);
		password.setMessage(Messages.HerokuLoginWizardPage_PasswordExample);
		password.setLayoutData(gd);
		password.addModifyListener(new ModifyListener()
		{

			@Override
			public void modifyText(ModifyEvent e)
			{
				getContainer().updateButtons();
			}
		});

		Button checkAuth = new Button(credentials, SWT.PUSH);
		checkAuth.setText(Messages.HerokuLoginWizardPage_SubmitButtonLabel);
		checkAuth.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				HerokuAPI api = new HerokuAPI(userId.getText(), password.getText());
				IStatus status = api.authenticate();
				if (!status.isOK())
				{
					setErrorMessage(status.getMessage());
				}
				else
				{
					api.writeCredentials();
					// if page is complete move on to next page
					if (isPageComplete())
					{
						getContainer().showPage(getNextPage());
					}
				}
			}
		});

		// Signup link
		Link link = new Link(composite, SWT.NONE);
		link.setText(Messages.HerokuLoginWizardPage_SignupLink);
		link.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				// Open the next dialog page where user can begin signup process!
				IWizardPage signupPage = new HerokuSignupPage(userId.getText());
				signupPage.setWizard(getWizard());
				getContainer().showPage(signupPage);
			}
		});

		Dialog.applyDialogFont(composite);
	}

	@Override
	public IWizardPage getNextPage()
	{
		IWizardPage nextPage = new HerokuDeployWizardPage();
		nextPage.setWizard(getWizard());
		return nextPage;
	}

	@Override
	public boolean isPageComplete()
	{
		String userId = this.userId.getText();
		if (userId == null || userId.trim().length() < 1)
		{
			setErrorMessage(Messages.HerokuLoginWizardPage_EmptyUserIDError);
			return false;
		}

		String password = this.password.getText();
		if (password == null || password.trim().length() < 1)
		{
			setErrorMessage(Messages.HerokuLoginWizardPage_EmptyPasswordError);
			return false;
		}

		setErrorMessage(null);
		return true;
	}

}
