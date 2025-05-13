```mermaid
erDiagram
    Users ||--o{ Inventory : manages
    Users ||--o{ Orders : places
    Users ||--o{ Delivery : has
    Users ||--o{ Deleted_Notifications : has
    Users ||--o{ Read_Notifications : has
    Users ||--o{ Sales : makes

    Inventory ||--o{ Order_Items : contains
    Inventory ||--o{ Sales : generates
    Inventory ||--|| Product_Prices : has

    Orders ||--o{ Order_Items : contains
    Orders ||--|| Delivery : has

    Users {
        int user_id PK
        string fullname
        string email
        string password
    }

    Inventory {
        int _id PK
        int user_id FK
        string Product_Name
        string Product_Category
        int Product_Qty
        float Product_Price
        int Product_Min_Threshold
    }

    Orders {
        int id PK
        date order_date
        time order_time
        float order_total
        string order_status
    }

    Order_Items {
        int id PK
        int order_id FK
        int product_id FK
        int quantity
        float price
        float total
    }

    Delivery {
        int id PK
        string order_description
        date delivery_date
        time delivery_time
        string status
        string location
    }

    Sales {
        int id PK
        int product_id FK
        int quantity
        float total_amount
        date sale_date
    }

    Product_Prices {
        string product_name
        string category
        float price
    }

    Deleted_Notifications {
        string notification_id
        int user_id FK
        datetime deleted_at
    }

    Read_Notifications {
        string notification_id
        int user_id FK
        datetime read_at
    }
} 