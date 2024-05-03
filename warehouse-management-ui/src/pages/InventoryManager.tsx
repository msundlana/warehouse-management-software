import React,{ FormEvent, useEffect, useState } from 'react'
import { ArticleDTO, JWTResponse, ProductArticleDTO, ProductDTO, WarehouseControllerApi } from '../services/generated/openapi';
import { useNavigate } from "react-router-dom"
import Layout from "../components/Layout"

export interface Product{
    name?:string;
    contain_articles?: Array<ProductArticle>
}

export interface ProductArticle{
    art_id:number;
    amount_of:number
}

export interface Article{
    art_id?:number;
    name?:string;
    stock?:number;
}

function InventoryManager({
    username: Username,
    jwtToken: Token,
    roles:Roles
}: JWTResponse){
    const navigate = useNavigate();
    const [username,setUserName] = useState(Username);
    const [jwtToken,setJWTToken] = useState(Token);
    const [roles,setRoles] = useState(Roles);
    const [articles,setArticles] = useState({});
    const [products,setProducts] = useState({});
    const [isSubmitting, setIsSubmitting] = useState(false);
    const [message,setMessage] = useState('');
    const [validationError, setValidationError] = useState('');
    
    useEffect(()=>{
        const stringUser = sessionStorage.getItem('user');
        if(!stringUser){
            navigate("/");
        }else{
            const user:JWTResponse = JSON.parse(stringUser)
            if(!user.roles?.includes('ROLE_ADMIN')){
                navigate("/error-page");
            }
            setUserName(user.username);
            setJWTToken(user.jwtToken);
            setRoles(user.roles);
        }
        
    },[]);

    const loadArticles= (e:FormEvent) => {
        e.preventDefault();
        setIsSubmitting(true);
        setMessage('');
        setValidationError('');


        const warehouseService:WarehouseControllerApi = new WarehouseControllerApi();
        warehouseService.loadArticles({articleDTO:articles as Array<ArticleDTO>},
            {
                headers:{
                    Authorization: `Bearer ${jwtToken}`
                  }
            })
        .then( (response) => {
            console.log('response',response);
                setIsSubmitting(false);
                setMessage(response.data);
        }).catch((error) => {
            setIsSubmitting(false);
            if(error.response && error.response.data ){
                setValidationError(error.response.data.message)
            }
        }
        );
    }

    const handleChangeArticles = (e:any) => {
        const file = e.target.files[0];
        const reader = new FileReader();
        setMessage('');
        setValidationError('');

        reader.onload = (e) => {
            if(e && e.target && e.target.result){
                try {
                    const result = JSON.parse(e.target.result as string);
                    const articles = result.inventory?result.inventory:null;
                    const articleDTOList:Array<ArticleDTO> = articles.map((article:Article)=>{
                        return {
                            id:article.art_id,
                            name:article.name,
                            stock:article.stock} as ArticleDTO
                    })
                    if(articleDTOList.length==0){
                        setValidationError('Data could not be mapped, check file content')
                    }else{
                        setArticles(articleDTOList);
                    } 
                
                } catch (error) {
                    setValidationError(error as string);
                }
            }
            
        };

        reader.readAsText(file);

    }

    const loadProducts = (e:FormEvent) => {
        e.preventDefault();
        setIsSubmitting(true);
        setMessage('');
        setValidationError('');
        const warehouseService:WarehouseControllerApi = new WarehouseControllerApi();
        warehouseService.loadProducts({productDTO:products as Array<ProductDTO>}, {
            headers:{
                Authorization: `Bearer ${jwtToken}`
              }
        })
        .then( (response) => {
            console.log('response',response);
                setIsSubmitting(false);
                setMessage(response.data);
        }).catch((error) => {
            setIsSubmitting(false);
            if(error.response && error.response.data ){
                setValidationError(error.response.data.message)
            }
        }
        );
    }

    const handleChangeProducts = (e:any) => {
        const file = e.target.files[0];
        const reader = new FileReader();
        setMessage('');
        setValidationError('');

        reader.onload = (e) => {
            if(e && e.target && e.target.result){
                try {
                    const result = JSON.parse(e.target.result as string);
                    const products = result.products?result.products:null;
                    const productDTOList:Array<ProductDTO> = products.map((product:Product)=>{
                        const productArticles = product.contain_articles?.map((productArticle)=>{
                            return {
                                id:0,
                                productId: 0,
                                articleId: productArticle.art_id,
                                quantity: productArticle.amount_of
                            } as ProductArticleDTO
                        });
                        return {
                            id:0,
                            name:product.name,
                            price:0,
                            articles:productArticles

                        } as ProductDTO
                    })

                    if(productDTOList.length==0){
                        setValidationError('Data could not be mapped, check file content')
                    }else{
                        setProducts(productDTOList);
                    } 
                    
                
                } catch (error) {
                    setValidationError(error as string)
                }
            }
            
        };

        reader.readAsText(file);
    }

    const logoutAction = () => {
        sessionStorage.removeItem('user');
        navigate("/");
    }

    return (
        <Layout>
            <div className='row justify-content-md-center'>
                <div className='col-12'>
                    <nav className='navbar navbar-expand-lg navbar-light bg-light'>
                    <div className="container-fluid">
                            <a className="navbar-brand" href="/dashboard">Dashboard</a>
                            <a className="navbar-brand" href="#">Inventory Manager</a>
                            <div className="d-flex">
                                <ul className="navbar-nav">
                                    <li className="nav-item">
                                        <a onClick={()=>logoutAction()} className="nav-link " aria-current="page" href="#">Logout</a>
                                    </li>
                                </ul>
                            </div>
                        </div>
                    </nav>
                    <h2 className="text-center mt-5">Welcome to The Warehouse Management Screen, {username}!</h2>
                </div>
            </div>
            <div className='row justify-content-md-center'>
                <div className='col-12'>
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
                <div className='col-6'>
                    <div className='text-center'><h4>Load Articles Inventory</h4></div>
                    
                    <form className='' onSubmit={(e)=>{loadArticles(e)}}>
                        <div className='mb-3'>
                            <label htmlFor='articles' className='form-label'>Articles</label>
                            <input id='articles'
                                    name='articles'
                                    type='file'
                                    accept=".json"
                                    className='form-control'
                                    onChange={e => handleChangeArticles(e)}
                            />
                        </div>
                        <div className='d-grid gap-2'>
                            <button
                                type='submit'
                                disabled={isSubmitting}
                                className='btn btn-secondary btn-block'
                                >Load Articles</button>
                        </div>
                        
                    </form>
                    
                </div>
                <div className='col-6'>
                    <div className='text-center'><h4>Load Product</h4></div>
                    <form className='' onSubmit={(e)=>{loadProducts(e)}}>
                        <div className='mb-3'>
                            <label htmlFor='products' className='form-label'>Products</label>
                            <input id='products'
                                    name='products'
                                    type='file'
                                    className='form-control'
                                    onChange={e => handleChangeProducts(e)}
                            />
                        </div>
                        <div className='d-grid gap-2'>
                            <button
                                type='submit'
                                disabled={isSubmitting}
                                className='btn btn-secondary btn-block'
                                >Load Products</button>
                        </div>
                    </form>
                </div>

            </div>
        </Layout>
    );

}

export default InventoryManager;