<%@taglib prefix="ofm" uri="http://www.siberhus.com/taglibs/ckeditor/ofm"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">

	<head>
		<meta http-equiv="content-type" content="text/html; charset=utf-8" />

        <title>File Manager</title>
		<% request.setAttribute("ctx", application.getContextPath()); %>
		<link rel="stylesheet" type="text/css" href="${ctx }/ckeditor/ofm/styles/reset.css" />
		<link rel="stylesheet" type="text/css" href="${ctx }/ckeditor/ofm/scripts/jquery.filetree/jqueryFileTree.css" />
		<link rel="stylesheet" type="text/css" href="${ctx }/ckeditor/ofm/scripts/jquery.contextmenu/jquery.contextMenu-1.01.css" />
		<link rel="stylesheet" type="text/css" href="${ctx }/ckeditor/ofm/styles/filemanager.css" />
		<!--[if IE]>
		<link rel="stylesheet" type="text/css" href="${ctx }/ckeditor/ofm/styles/ie.css" />
		<![endif]-->

        <script type="text/javascript" src="${ctx }/ckeditor/ofm/scripts/jquery-1.6.2.min.js"></script>
		
        <script type="text/javascript">
            var ofmBase ="${ctx}/ckeditor/ofm";
            var culture = '<ofm:currentLocale />';
            var autoload = true;
            var showFullPath = false;
            var browseOnly = false;
            var fileRoot = '/';
          	var fileConnector = '${param.fileConnector}';
            var tmp = '${param.showThumbs}';
            var showThumbs = (tmp == '' ? false : eval(tmp));
            var space = '${param.space}';
            var type = '${param.type}';
            var webRoot = '<ofm:baseUrl type="${param.space }" space="${param.type }"/>';
        </script>
        
        <script type="text/javascript" src="${ctx }/ckeditor/ofm/scripts/jquery.form-2.84.js"></script>
        <script type="text/javascript" src="${ctx }/ckeditor/ofm/scripts/jquery.splitter/jquery.splitter-1.5.1.js"></script>
        <script type="text/javascript" src="${ctx }/ckeditor/ofm/scripts/jquery.filetree/jqueryFileTree.js"></script>
        <script type="text/javascript" src="${ctx }/ckeditor/ofm/scripts/jquery.contextmenu/jquery.contextMenu-1.01.js"></script>
        <script type="text/javascript" src="${ctx }/ckeditor/ofm/scripts/jquery.impromptu-3.1.min.js"></script>
        <script type="text/javascript" src="${ctx }/ckeditor/ofm/scripts/jquery.tablesorter-2.0.5b.min.js"></script>
        <script type="text/javascript" src="${ctx }/ckeditor/ofm/scripts/filemanager.js"></script>
	</head>

    <body>
        <div>
            <form id="uploader" method="post" enctype="multipart/form-data">
                <button id="home" name="home" type="button" value="Home">&nbsp;</button>
                <h1></h1>
                <div id="uploadresponse"></div>
                <input id="mode" name="mode" type="hidden" value="add" />
                <input id="currentpath" name="currentpath" type="hidden" />
                <input id="newfile" name="newfile" type="file" />
                <button id="upload" name="upload" type="submit" value="Upload"></button>
                <button id="newfolder" name="newfolder" type="button" value="New Folder"></button>
                <button id="grid" class="ON" type="button">&nbsp;</button><button id="list" type="button">&nbsp;</button>
            </form>

            <div id="splitter">
                <div id="filetree"></div>
                <div id="fileinfo"><h1></h1></div>
            </div>

            <ul id="itemOptions" class="contextMenu">
                <li class="select"><a href="#select"></a></li>
                <li class="download"><a href="#download"></a></li>
                <li class="rename"><a href="#rename"></a></li>
                <li class="delete separator"><a href="#delete"></a></li>
            </ul>
        </div>
		
    </body>
</html>