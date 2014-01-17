import java.io.File;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import org.eclipse.jgit.api.AddCommand;
import org.eclipse.jgit.api.CommitCommand;
import org.eclipse.jgit.api.DiffCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.PullCommand;
import org.eclipse.jgit.api.PullResult;
import org.eclipse.jgit.api.PushCommand;
import org.eclipse.jgit.api.ReflogCommand;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.dircache.DirCache;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.ReflogEntry;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.TextProgressMonitor;
import org.eclipse.jgit.revplot.PlotCommit;
import org.eclipse.jgit.revplot.PlotCommitList;
import org.eclipse.jgit.revplot.PlotLane;
import org.eclipse.jgit.revplot.PlotWalk;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.transport.PushResult;
/*
 * User_Guide : http://wiki.eclipse.org/JGit/User_Guide 
 * DOCS       : http://download.eclipse.org/jgit/docs/latest/apidocs/ 
 * Git book   : http://git-scm.com/book/ko/
 * */
public class JGitExample {
	public static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public static String bareRepPath   = "C:/LeeJS/document/gerrit/git_test_reps/bare_rep";
	public static String serverRepPath = "C:/LeeJS/document/gerrit/git_test_reps/server_rep";
	public static String deviceRepPath = "C:/LeeJS/document/gerrit/git_test_reps/device_{$}_rep";
	public static void main(String[] args) {
		try {
			//1. repository 생성
//			createRepository(bareRepPath);
			
			//2. clone
//			cloneRepository(bareRepPath, serverRepPath);
//			cloneRepository(bareRepPath, deviceRepPath.replace("{$}", "tuser1"));
//			cloneRepository(bareRepPath, deviceRepPath.replace("{$}", "tuser2"));
			
			//3. get repository
//			FileRepository rep_bare    = new FileRepository(bareRepPath + "/.git");
			FileRepository rep_server  = new FileRepository(serverRepPath + "/.git");
			FileRepository rep_device1 = new FileRepository(deviceRepPath.replace("{$}", "tuser1") + "/.git");
//			FileRepository rep_device2 = new FileRepository(deviceRepPath.replace("{$}", "tuser2") + "/.git");
			
			
			//4. add
			add(rep_server);
			//5. commit
			commit(rep_server, "add tuser3");
			//6. push
			push(rep_server);
			//7. pull
//			pull(rep_device1);
		} catch (Exception e) {e.printStackTrace();}
	}
	/*
	 * Repository create
	 * */
	public static void createRepository(String path) throws Exception{
		Repository repository = new FileRepository(path);
		if(!repository.getFS().isDirectory(new File(path))){//존재하지 않을때만..
			repository.create();
		}
	}
	/*
	 * Repository clone
	 * Error : java.lang.NoClassDefFoundError: com/jcraft/jsch/JSchException
	 *  Add jar : jsch-0.1.50.jar
	 * */
	public static void cloneRepository(String originUrl, String newPath) throws Exception{
		System.out.println("<--start clone");
		Git.cloneRepository()
		   .setURI(originUrl)
		   .setDirectory(new File(newPath))
//		   .setBare(false)
//		   .setRemote("origin")
		   .call();
		System.out.println("end clone-->");
	}
	/*
	 * git add .
	 * */
	public static void add(Repository repository) throws Exception{
		AddCommand  command = new Git(repository).add();
		DirCache cache = command
						   .addFilepattern(".")//*.java, aa.txt
						   .call();
		System.out.println("add: " + cache);
	}
	/*
	 * git commit -am 'comment'
	 * */
	public static void commit(Repository repository, String message) throws Exception{
		CommitCommand command = new Git(repository).commit();
		RevCommit commit = command
						   .setMessage(message)
						   .setAll(true)// ??
						   .call();
		System.out.println("commit [" + commit.getShortMessage() + "][" + commit.getCommitterIdent().getName() + "<" + commit.getCommitterIdent().getEmailAddress() + ">" + "][" + dateFormat.format(commit.getCommitterIdent().getWhen().getTime()) + "]");
	}
	/*
	 * git push origin master
	 * */
	public static void push(Repository repository) throws Exception{
		PushCommand command = new Git(repository).push();
		Iterable<PushResult> it = command
								  .setRemote("origin")
								  .setPushAll()
								  .call();
		for(PushResult result : it){
			System.out.println("push " + repository.toString() + " --> " + result.getURI());
//			System.out.println("  Messages: " + result.getMessages());
		}
	}
	/*
	 * git pull
	 * */
	public static void pull(Repository repository) throws Exception{
		System.out.println("pull: " + repository);
		PullCommand command = new Git(repository).pull();
//		TextProgressMonitor monitor = new TextProgressMonitor();
		PullResult result = command
						  //.setCredentialsProvider(credentialsProvider)
						  //.setProgressMonitor(monitor)
						  .call();
		System.out.println(result.getFetchedFrom());
		System.out.println(result.getFetchResult().getMessages());
	}
}
