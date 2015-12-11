<?php


// Import our variables to connect to the database
require_once __DIR__ . '/variables.php';

// Create connection
$conn = mysqli_connect(hostname_db,username_db,password_db, database_db);
// Check connection
if (!$conn) {
    die("Connection failed: " . mysqli_connect_error());
}

//Get the user_id
$user_id = $_POST['user_id'];
$sql = "SELECT * FROM zone_description WHERE region_id IN ( SELECT zone_description_region_id FROM region_authority WHERE user_user_id ='".$user_id."' )";
$result = mysqli_query($conn, $sql);

$con = "";
$con .= "{ \"zone_obj\": [";

if (mysqli_num_rows($result) > 0) {
    // output data of each row
    while($row = mysqli_fetch_assoc($result)) {
		$con .= " { \"region_id\": ".$row["region_id"].", \"region_name\": \"".$row["region_name"]."\" }, ";
    }
	$con .= "{ \"region_id\": 0, \"region_name\": \"null\" } ] }";
	echo $con;
} 
else {
    echo "No Regions Found";
}
mysqli_close($conn);
?>
