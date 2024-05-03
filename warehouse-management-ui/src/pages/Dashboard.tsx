import React,{ FormEvent, useEffect, useState } from 'react'
import { JWTResponse, PageProductInventoryDTO, ProductInventoryDTO, WarehouseControllerApi } from '../services/generated/openapi';
// import axios from 'axios'
import { useNavigate } from "react-router-dom"
import Layout from "../components/Layout"

function Dashboard({
    username: Username,
    jwtToken: Token,
    roles:Roles
}: JWTResponse,{content:Content}:PageProductInventoryDTO){
    const navigate = useNavigate();
    const [username,setUserName] = useState(Username);
    const [jwtToken,setJWTToken] = useState(Token);
    const [roles,setRoles] = useState(Roles);
    const [isSubmitting, setIsSubmitting] = useState(false);
    const [validationError, setValidationError] = useState('');
    const [message,setMessage] = useState('');
    const [poductInventory,setPoductInventory] = useState(Content);
    const [pageNumber,setPageNumber] = useState(0);
    const [totalPages,setTotalPages] = useState(0);
    const [lastPage,setLastPage] = useState(false); 
    const [firstPage,setFirstPage] = useState(false); 

    useEffect(()=>{
        const stringUser = sessionStorage.getItem('user');
        if(!stringUser){
            navigate("/");
        }
        else{
            const user:JWTResponse = JSON.parse(stringUser);
            setUserName(user.username);
            setJWTToken(user.jwtToken);
            setRoles(user.roles);
            getProductInventory(user.jwtToken);
        }
    },[]);

    const getProductInventory = (jwtToken:string|undefined) => {
        setIsSubmitting(true);
        setValidationError('');
        const warehouseService:WarehouseControllerApi = new WarehouseControllerApi();
        warehouseService.getAllAvailableProducts({}, {
            headers:{
                Authorization: `Bearer ${jwtToken}`
              }
        })
        .then( (response) => {
                setIsSubmitting(false);
                let page:PageProductInventoryDTO = response.data;
                    setPoductInventory(page.content?page.content:[]);
                    setFirstPage(page.first?page.first:false);
                    setLastPage(page.last?page.last:false)
                    setPageNumber(page.number?page.number:0);
                    setTotalPages(page.totalPages?page.totalPages:0)
               
                
        }).catch((error) => {
            setIsSubmitting(false);
            if(error.response && error.response.data ){
                setValidationError(error.response.data.message)
            }
        }
        );
    }

    const sellProduct = (productId:number|undefined) => {
        setIsSubmitting(true);
        setValidationError('');
        setMessage('');
        const warehouseService:WarehouseControllerApi = new WarehouseControllerApi();
        if(productId!){
            warehouseService.sellProduct({
                productId:productId
            }, {
                headers:{
                    Authorization: `Bearer ${jwtToken}`
                }
            })
            .then( (response) => {
                    setIsSubmitting(false);
                    setMessage(response.data);
                    getProductInventory(jwtToken);
                    
            }).catch((error) => {
                setIsSubmitting(false);
                if(error.response && error.response.data ){
                    setValidationError(error.response.data.message)
                }
            }
            );
        }
    }

    const logoutAction = () => {
        sessionStorage.removeItem('user');
        navigate("/");
    }


    const getProductInventoryRow = poductInventory?.map((product:ProductInventoryDTO)=>{
        return (
            <tr key={product.id}>
                <td>{product.name}</td>
                <td>{product.price}</td>
                <td>{product.availableStock}</td>
                <td><button className='btn btn-secondary btn-block' onClick={()=>sellProduct(product.id)}>Sell</button></td>
            </tr>
        );
    })


    return (
        <Layout>
            <div className='row justify-content-md-center'>
                <div className='col-12'>
                    <nav className='navbar navbar-expand-lg navbar-light bg-light'>
                    <div className="container-fluid">
                            <a className="navbar-brand" href="#">Dashboard</a>
                            {roles?.includes("ROLE_ADMIN") && <a className="navbar-brand" href="/inventory-manager">Inventory Manager</a>}
                            <div className="d-flex">
                                <ul className="navbar-nav">
                                    <li className="nav-item">
                                        <a onClick={()=>logoutAction()} className="nav-link " aria-current="page" href="#">Logout</a>
                                    </li>
                                </ul>
                            </div>
                        </div>
                    </nav>
                    <h2 className="text-center mt-5">Welcome, {username}!</h2  >
                </div>
            </div>
            
            <div className='row justify-content-md-center'>
                <div className='col-12 text-center'>
                {
                         validationError && validationError.length>0 &&
                        <p className='text-center'>
                            <small className='text-danger'>{validationError}</small>
                        </p>
                }
                { message.length>0 && 
                    <p className='text-center'>
                        <small className='text-success'>{message}</small>
                    </p>
                }
                </div>
            </div>
            <div className='row justify-content-md-center'>
                <div className='col-12'>
                    {poductInventory &&
                    <table className="table table-striped">
                        <thead>
                        <tr>
                            <th>Product Name</th>
                            <th>Price</th>
                            <th>Available Stock</th>
                            <th>Selling</th>
                        </tr>
                        </thead>
                        <tbody>
                        {getProductInventoryRow}
                        </tbody>
                    </table>}
                </div>
            </div>
        </Layout>
    );

}

export default Dashboard;