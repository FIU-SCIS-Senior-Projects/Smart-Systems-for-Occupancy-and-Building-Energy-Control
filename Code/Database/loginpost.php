
<?php
$hostname_localhost ="localhost";
$database_localhost ="ssobec_db";
$username_localhost ="root";
$password_localhost ="root";
$localhost = mysql_connect($hostname_localhost,$username_localhost,$password_localhost)
or
trigger_error(mysql_error(),E_USER_ERROR);

mysql_select_db($database_localhost, $localhost);

$email = $_POST['email'];
$password = $_POST['password'];
$query_search = "select * from user where login_email = '".$email."' AND password = '".$password. "'";
$query_exec = mysql_query($query_search) or die(mysql_error());
$rows = mysql_num_rows($query_exec);
//echo $rows;
 if($rows == 0) {
 echo "User Not Found";
 }
 else  {
    echo "User Found";
}
?>
