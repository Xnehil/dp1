import Image from "next/image";

export default function IconoTarea({ icon, description, styleBg = "bg-gray-200", size = "70" }) {

  //const containerClasses = ;
  return (
    <div className={`flex flex-col w-[${size}px]`}>
      <div className={` ${styleBg} rounded-3xl flex items-center justify-center shadow-md w-${size} h-${size}`}>
        <Image src={icon} className="m-8" alt="Icon" width={size} height={size} />
      </div>
      <p className="mt-2 text-2xl text-gray-700 text-center">{description}</p>
    </div>
  );
}
