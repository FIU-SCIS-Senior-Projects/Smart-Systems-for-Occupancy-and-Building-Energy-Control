<?php

// Import our variables to connect to the database
require_once __DIR__ . '/variables.php';

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

$notification_result = TRUE;
$mail_resutl = TRUE;
$str_res = ": ";

$list = array();

if($result) {
    if(mysqli_num_rows($result) > 0){ 
        while ($row = mysqli_fetch_assoc($result)) {
	    $list[] = array('login_email' => $row['login_email']);
	}
        echo json_encode($list, JSON_FORCE_OBJECT);       
    }
}
else {
    echo 'Mysql Error 1: '. mysqli_error($conn);
}

mysqli_close($conn);
?>
