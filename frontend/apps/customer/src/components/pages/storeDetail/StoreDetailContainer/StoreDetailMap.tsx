const StoreDetailMap = () => {
  return (
    <div className="flex h-80 flex-col items-start justify-start gap-2.5 self-stretch overflow-hidden bg-white px-5 py-3.5">
      <div className="justify-center text-center font-['Noto_Sans_KR'] text-base font-bold leading-none text-black">
        위치
      </div>
      <img
        className="h-60 self-stretch rounded-[10px]"
        src="https://placehold.co/384x237"
      />
    </div>
  );
};

export default StoreDetailMap;
