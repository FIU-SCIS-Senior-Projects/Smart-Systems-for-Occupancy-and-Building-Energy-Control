<?php
ini_set('display_errors',1);
ini_set('display_startup_errors',1);
error_reporting(-1);

// Import our variables to connect to the database
require_once __DIR__ . '/variables.php';
require_once __DIR__ . '/libs/PHPMailer/PHPMailerAutoload.php';
//require_once 'libs/PHPMailer/PHPMailerAutoload.php';

$m = new PHPMailer;

$m->isSMTP();
$m->SMTPAuth = true;
//$m->SMTPDebug = 1;
$m->Host = 'smtp.gmail.com';
$m->Username = 'fiussobec@gmail.com';
$m->Password = 'ldtcztvukeohecxr';
$m->SMTPSecure = 'tls';

$m->Port = 587;

// Create connection
$conn = mysqli_connect(hostname_db,username_db,password_db, database_db);
// Check connection
if (!$conn) {
    die("Connection failed: " . mysqli_connect_error());
}

$region_name = $_POST['region_name'];
$login_email = $_POST['login_email'];

$user_id = $_POST['user_id'];
$rewards_total = $_POST['total_rewards'];
$points = $_POST['reward'];
$description = $_POST['reward_description'];

$region_id = -1;

//$sql = "SELECT region_id FROM zone_description WHERE region_name ='" . $region_name . "';";

$sql = "SELECT login_email FROM user, (SELECT user_user_id FROM (SELECT region_id FROM zone_description WHERE region_name = '" . $region_name ."') as region_id, region_authority WHERE zone_description_region_id = region_id) as user_ids WHERE user_user_id = user_id;";

$result = mysqli_query($conn, $sql);

//echo $sql;

$notification_result = 1;
$mail_result = 1;
$str_res = ": ";
if($result) {
    if(mysqli_num_rows($result) > 0){ 
       
       	$sql2 = "INSERT INTO user_rewards (user_id, description, points) VALUES ('".$user_id."', '" . $description . "', '".$points."')";
        //echo $sql2;
	$result2 = mysqli_query($conn, $sql2);
	
	if($result2){
	    $notification_result = $notification_result and  TRUE;
	}
	else {
	    $str_res = $str_res . " ,add user rewards ";
	}
        
	$sql3 = "UPDATE user SET rewards_total='".$rewards_total."' WHERE user_id='".$user_id."'";
	$res3 = mysqli_query($conn, $sql3);    
        
	if($res3) {
	   //$notification_result = $notification_result and TRUE;
	  $notification_result = 1;
	}
	else {
	    $str_res = $str_res . " ,set total rewards ";
	}

        while ($row = mysqli_fetch_assoc($result)) {
	   $m->From = $login_email;
	   $m->FromName = 'FIU SSOBEC';
	   $m->addReplyTo($login_email, 'Reply address');
	   $m->addAddress($row["login_email"], "Zone Subscriber");
	   $m->Subject = "SSOBEC Notification";
	   $m->Body = "Save Energy by reducing consumption and turning appliances and lights off!";

	   //$mail_single_result = (boolval( $m->send()) ? 'true' : 'false') ;
	   //echo 'SINGLE RESULT' . $mail_single_result;
	   //$mail_result = $mail_result and $mail_single_result;
	   //echo 'MAIL Result' . $mail_result;
	   $mail_single_result = $m->send();
	   $mail_result = $mail_result & $mail_single_result;
        }

	//echo 'NOTIFICATION RESULT' . $notification_result;
	//$notification_result = (boolval($notification_result) ? 'true' : 'false');

	//if( ($mail_result and $notification_result ) == TRUE ){
	if( ($mail_result and $notification_result) == 1 ) {
            echo "Successful";
	}
        else {
            echo "Failed to send notification" . $str_res;
	}
    }
}
else {
    echo 'Mysql Error 1: '. mysqli_error($conn);
}

mysqli_close($conn);
?>
