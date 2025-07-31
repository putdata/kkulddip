export interface FoodItem {
  id: number;
  name: string;
  imageUrl: string;
  description: string;
}

interface LoginFoodCardProps {
  item: FoodItem;
}

const LoginFoodCard = ({ item }: LoginFoodCardProps) => {
  return (
    <div className="overflow-hidden rounded-xl shadow-md">
      <img
        src={item.imageUrl}
        alt={item.name}
        className="block h-48 w-full object-cover"
      />
      <div className="p-4">
        <h2 className="text-base font-semibold">{item.description}</h2>
        <p className="text-sm text-gray-500">{item.name} 어때요?</p>
      </div>
    </div>
  );
};

export default LoginFoodCard;
