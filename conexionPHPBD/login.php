<?PHP
$hostname_localhost ="localhost";
$database_localhost ="tracker_system_db";
$username_localhost ="root";
$password_localhost ="";

$json=array();

	if(isset($_GET["user"])){
		$user=$_GET["user"];
		$password=$_GET["password"];
				
		$conexion = mysqli_connect($hostname_localhost,$username_localhost,$password_localhost,$database_localhost);

		$consulta="select user,password,role from t_users where user= '{$user}' and password= '{$password}'";
		
		$resultado=mysqli_query($conexion,$consulta);
		
			
		if($registro=mysqli_fetch_array($resultado)){
				
			//$json['userData'][]="User: ".$registro[0]." Password: ".$registro[1]." Role: ".$registro[2]."}";
			
			$json = ['User' => $registro[0], 'Password' => $registro[1], 'Role' => $registro[2]];
			
	
		}else{
			$resultar["user"]="";
			$resultar["password"]='no registra';
			$resultar["role"]='no registra';
			$json['usuario'][]=$resultar;
		}
		
		mysqli_close($conexion);
		echo json_encode($json);
	}
	else{
		$resultar["user"]="";
		$resultar["password"]='no registra';
		$resultar["role"]='no registra';
		$json['usuario'][]=$resultar;
		echo json_encode($json);
	}
?>